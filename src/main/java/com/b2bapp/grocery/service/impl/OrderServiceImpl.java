package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.dto.TopCategoryDTO;
import com.b2bapp.grocery.dto.TopWholesalerDTO;
import com.b2bapp.grocery.dto.TotalSalesStatsDTO;
import com.b2bapp.grocery.exception.InsufficientStockException;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.CartItemRepository;
import com.b2bapp.grocery.repository.OrderRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.OrderService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order checkout(String retailerEmail) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        List<CartItem> cartItems = cartItemRepository.findByRetailer(retailer);
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        // Step 1: Check and reduce product stock
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int availableQty = product.getQuantity();
            int orderedQty = item.getQuantity();

            if (orderedQty > availableQty) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Reduce stock
            product.setQuantity(availableQty - orderedQty);
        }

        // Step 2: Convert CartItems to OrderItems
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> OrderItem.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .retailer(retailer)
                .items(orderItems)
                .orderDate(LocalDateTime.now())
                .totalAmount(total)
                .status(OrderStatus.PLACED)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        cartItemRepository.deleteByRetailer(retailer); // Clear cart

        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrders(String retailerEmail, int page, int size) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new RuntimeException("Retailer not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByRetailer(retailer, pageable);
    }

    @Override
    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getOrdersByWholesalerEmail(String wholesalerEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findOrdersByWholesalerEmail(wholesalerEmail, pageable);
    }



    @Override
    public List<ProductSalesStatsDTO> getWholesalerProductStats(String email, String category, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findOrdersByItems_Product_Wholesaler_Email(email);

        Map<String, ProductSalesStatsDTO> stats = new HashMap<>();

        for (Order order : orders) {
            if (startDate != null && order.getOrderDate().toLocalDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().toLocalDate().isAfter(endDate)) continue;

            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (!product.getWholesaler().getEmail().equals(email)) continue;
                if (category != null && !product.getCategory().equalsIgnoreCase(category)) continue;

                stats.compute(product.getName(), (k, v) -> {
                    double revenue = item.getQuantity() * item.getPriceAtPurchase();
                    if (v == null) {
                        return new ProductSalesStatsDTO(k, item.getQuantity(), revenue);
                    } else {
                        v.setTotalUnitsSold(v.getTotalUnitsSold() + item.getQuantity());
                        v.setTotalRevenue(v.getTotalRevenue() + revenue);
                        return v;
                    }
                });
            }
        }

        return new ArrayList<>(stats.values());
    }


    @Override
    public List<ProductSalesStatsDTO> getTopSellingProducts(String email, String category, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findOrdersByItems_Product_Wholesaler_Email(email);

        Map<String, ProductSalesStatsDTO> stats = new HashMap<>();

        for (Order order : orders) {
            if (startDate != null && order.getOrderDate().toLocalDate().isBefore(startDate)) continue;
            if (endDate != null && order.getOrderDate().toLocalDate().isAfter(endDate)) continue;

            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (!product.getWholesaler().getEmail().equals(email)) continue;
                if (category != null && !product.getCategory().equalsIgnoreCase(category)) continue;

                stats.compute(product.getName(), (k, v) -> {
                    double revenue = item.getQuantity() * item.getPriceAtPurchase();
                    if (v == null) {
                        return new ProductSalesStatsDTO(k, item.getQuantity(), revenue);
                    } else {
                        v.setTotalUnitsSold(v.getTotalUnitsSold() + item.getQuantity());
                        v.setTotalRevenue(v.getTotalRevenue() + revenue);
                        return v;
                    }
                });
            }
        }

        // Sort by units sold descending
        return stats.values().stream()
                .sorted(Comparator.comparingInt(ProductSalesStatsDTO::getTotalUnitsSold).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Order> filterOrdersForAdmin(String retailerEmail, String wholesalerEmail, String category, LocalDate start, LocalDate end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        return orderRepository.findAll((root, query, cb) -> {
            Join<Object, Object> itemJoin = root.join("items");
            Join<Object, Object> productJoin = itemJoin.join("product");

            Predicate predicate = cb.conjunction();

            if (retailerEmail != null && !retailerEmail.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("retailer").get("email"), retailerEmail));
            }

            if (wholesalerEmail != null && !wholesalerEmail.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(productJoin.get("wholesaler").get("email"), wholesalerEmail));
            }

            if (category != null && !category.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(productJoin.get("category")), category.toLowerCase()));
            }

            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("orderDate").as(LocalDate.class), start));
            }

            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("orderDate").as(LocalDate.class), end));
            }

            return predicate;
        }, pageable);
    }










    @Override
    public Page<Order> filterRetailerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size) {
        User retailer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Retailer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        return orderRepository.findAll((root, query, cb) -> {
            Join<Object, Object> itemJoin = root.join("items");
            Join<Object, Object> productJoin = itemJoin.join("product");

            Predicate predicate = cb.equal(root.get("retailer"), retailer);

            if (category != null && !category.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(productJoin.get("category")), category.toLowerCase()));
            }
            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("orderDate").as(LocalDate.class), start));
            }
            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("orderDate").as(LocalDate.class), end));
            }

            return predicate;
        }, pageable);
    }

    @Override
    public Page<Order> filterWholesalerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        return orderRepository.findAll((root, query, cb) -> {
            Join<Object, Object> itemJoin = root.join("items");
            Join<Object, Object> productJoin = itemJoin.join("product");

            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(productJoin.get("wholesaler").get("email"), email));

            if (category != null && !category.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(productJoin.get("category")), category.toLowerCase()));
            }

            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("orderDate").as(LocalDate.class), start));
            }

            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("orderDate").as(LocalDate.class), end));
            }

            return predicate;
        }, pageable);
    }




    @Override
    public TotalSalesStatsDTO getTotalSalesStatsForPeriod(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDateTime date = order.getOrderDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end) || date.isBefore(end));
                })
                .toList();

        int totalOrders = orders.size();
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        return TotalSalesStatsDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .build();
    }


    @Override
    public List<TopWholesalerDTO> getTop5Wholesalers(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDateTime date = order.getOrderDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end) || date.isBefore(end));
                })
                .toList();

        Map<String, TopWholesalerDTO> wholesalerStats = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                User wholesaler = product.getWholesaler();

                String email = wholesaler.getEmail();
                wholesalerStats.compute(email, (key, existing) -> {
                    long qty = item.getQuantity();
                    double revenue = qty * item.getPriceAtPurchase();
                    if (existing == null) {
                        return TopWholesalerDTO.builder()
                                .wholesalerName(wholesaler.getName())
                                .wholesalerEmail(email)
                                .totalUnitsSold(qty)
                                .totalRevenue(revenue)
                                .build();
                    } else {
                        existing.setTotalUnitsSold(existing.getTotalUnitsSold() + qty);
                        existing.setTotalRevenue(existing.getTotalRevenue() + revenue);
                        return existing;
                    }
                });
            }
        }

        return wholesalerStats.values().stream()
                .sorted(Comparator.comparingDouble(TopWholesalerDTO::getTotalRevenue).reversed())
                .limit(5)
                .toList();
    }



    @Override
    public List<TopCategoryDTO> getTop5Categories(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDateTime date = order.getOrderDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end) || date.isBefore(end));
                })
                .toList();

        Map<String, TopCategoryDTO> categoryStats = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                String category = product.getCategory();

                categoryStats.compute(category, (key, existing) -> {
                    long qty = item.getQuantity();
                    double revenue = qty * item.getPriceAtPurchase();
                    if (existing == null) {
                        return TopCategoryDTO.builder()
                                .category(category)
                                .totalUnitsSold(qty)
                                .totalRevenue(revenue)
                                .build();
                    } else {
                        existing.setTotalUnitsSold(existing.getTotalUnitsSold() + qty);
                        existing.setTotalRevenue(existing.getTotalRevenue() + revenue);
                        return existing;
                    }
                });
            }
        }

        return categoryStats.values().stream()
                .sorted(Comparator.comparingDouble(TopCategoryDTO::getTotalUnitsSold).reversed())
                .limit(5)
                .toList();
    }

}

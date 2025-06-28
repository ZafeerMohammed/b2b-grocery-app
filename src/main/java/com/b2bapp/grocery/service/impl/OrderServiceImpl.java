package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.*;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            if (order.getStatus() != OrderStatus.DELIVERED) continue;

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
            if (order.getStatus() != OrderStatus.DELIVERED) continue;

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
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
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
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .filter(order -> {
                    LocalDateTime date = order.getOrderDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end) || date.isBefore(end));
                })
                .toList();

        Map<String, TopWholesalerDTO> wholesalerStats = new HashMap<>();

        for (Order order : orders) {

            if (order.getStatus() != OrderStatus.DELIVERED) continue;

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
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
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



    @Override
    public List<MonthlySalesDTO> getMonthlySalesOverview(int year) {
        List<Order> orders = orderRepository.findByActiveTrueAndOrderDateBetween(
                LocalDate.of(year, 1, 1).atStartOfDay(),
                LocalDate.of(year, 12, 31).atTime(23, 59, 59)
        ).stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).toList();

        Map<Integer, MonthlySalesDTO> monthlyStats = new HashMap<>();

        for (Order order : orders) {

            int month = order.getOrderDate().getMonthValue();
            String monthName = order.getOrderDate().getMonth().name().substring(0, 1).toUpperCase() +
                    order.getOrderDate().getMonth().name().substring(1).toLowerCase() +
                    " " + year;

            double revenue = order.getItems().stream()
                    .mapToDouble(i -> i.getQuantity() * i.getPriceAtPurchase())
                    .sum();

            monthlyStats.compute(month, (k, v) -> {
                if (v == null) {
                    return new MonthlySalesDTO(monthName, 1, revenue);
                } else {
                    v.setTotalOrders(v.getTotalOrders() + 1);
                    v.setTotalRevenue(v.getTotalRevenue() + revenue);
                    return v;
                }
            });
        }

        return monthlyStats.values().stream()
                .sorted(Comparator.comparing(dto -> LocalDate.parse("01 " + dto.getMonth(), DateTimeFormatter.ofPattern("dd MMMM yyyy"))))
                .toList();
    }




    @Override
    public List<TopRetailerDTO> getTopRetailers(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByActiveTrueAndOrderDateBetween(start, end);

        Map<String, TopRetailerDTO> retailerMap = new HashMap<>();

        for (Order order : orders) {
            String email = order.getRetailer().getEmail();
            String name = order.getRetailer().getName();
            if (order.getStatus() != OrderStatus.DELIVERED) continue;


            double amount = order.getItems().stream()
                    .mapToDouble(i -> i.getQuantity() * i.getPriceAtPurchase())
                    .sum();

            retailerMap.compute(email, (key, value) -> {
                if (value == null) {
                    return new TopRetailerDTO(name, email, 1, amount);
                } else {
                    value.setTotalOrders(value.getTotalOrders() + 1);
                    value.setTotalSpent(value.getTotalSpent() + amount);
                    return value;
                }
            });
        }

        return retailerMap.values().stream()
                .sorted(Comparator.comparingDouble(TopRetailerDTO::getTotalSpent).reversed())
                .limit(5)
                .toList();
    }


    @Override
    public List<RecentOrderDTO> getRecentOrders() {
        return orderRepository.findTop10ByActiveTrueOrderByOrderDateDesc().stream()
                .map(order -> {
                    double total = order.getItems().stream()
                            .mapToDouble(item -> item.getQuantity() * item.getPriceAtPurchase())
                            .sum();
                    return RecentOrderDTO.builder()
                            .orderId(order.getId())
                            .retailerName(order.getRetailer().getName())
                            .retailerEmail(order.getRetailer().getEmail())
                            .orderDate(order.getOrderDate())
                            .totalAmount(total)
                            .build();
                })
                .toList();
    }


    @Override
    public TotalSalesStatsDTO getTotalSalesStatsForWholesaler(String wholesalerEmail, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findOrdersByWholesalerEmailAndDateRange(wholesalerEmail, start, end)
                .stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).toList();

        int totalOrders = orders.size();
        double totalRevenue = 0;

        for (Order order : orders) {
            totalRevenue += order.getItems().stream()
                    .filter(item -> item.getProduct().getWholesaler().getEmail().equals(wholesalerEmail))
                    .mapToDouble(item -> item.getQuantity() * item.getPriceAtPurchase())
                    .sum();
        }

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        return TotalSalesStatsDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .build();
    }



    @Override
    public List<TopRetailerForWholesalerDTO> getTopRetailersForWholesaler(String wholesalerEmail, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findOrdersByWholesalerEmailAndDateRange(wholesalerEmail, start, end)
                .stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).toList();

        Map<String, TopRetailerForWholesalerDTO> retailerMap = new HashMap<>();

        for (Order order : orders) {
            String email = order.getRetailer().getEmail();
            String name = order.getRetailer().getName();

            double totalSpent = order.getItems().stream()
                    .filter(item -> item.getProduct().getWholesaler().getEmail().equals(wholesalerEmail))
                    .mapToDouble(item -> item.getQuantity() * item.getPriceAtPurchase())
                    .sum();

            long units = order.getItems().stream()
                    .filter(item -> item.getProduct().getWholesaler().getEmail().equals(wholesalerEmail))
                    .mapToLong(item -> item.getQuantity())
                    .sum();

            retailerMap.merge(email,
                    new TopRetailerForWholesalerDTO(name, email, units, totalSpent),
                    (existing, newOne) -> {
                        existing.setTotalUnitsBought(existing.getTotalUnitsBought() + newOne.getTotalUnitsBought());
                        existing.setTotalSpent(existing.getTotalSpent() + newOne.getTotalSpent());
                        return existing;
                    });
        }

        return retailerMap.values().stream()
                .sorted(Comparator.comparingDouble(TopRetailerForWholesalerDTO::getTotalSpent).reversed())
                .limit(5)
                .toList();
    }



    @Override
    public List<RecentOrderDTO> getRecentOrdersForWholesaler(String wholesalerEmail) {
        List<Order> orders = orderRepository.findTop10ByWholesalerEmailOrderByOrderDateDesc(wholesalerEmail);

        return orders.stream().map(order -> {
            double totalAmount = order.getItems().stream()
                    .filter(item -> item.getProduct().getWholesaler().getEmail().equals(wholesalerEmail))
                    .mapToDouble(item -> item.getQuantity() * item.getPriceAtPurchase())
                    .sum();

            return RecentOrderDTO.builder()
                    .orderId(order.getId())
                    .retailerName(order.getRetailer().getName())
                    .retailerEmail(order.getRetailer().getEmail())
                    .orderDate(order.getOrderDate())
                    .totalAmount(totalAmount)
                    .build();
        }).toList();
    }


    public boolean cancelOrderIfPossible(UUID orderId, String retailerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getRetailer().getEmail().equalsIgnoreCase(retailerEmail)) {
            throw new AccessDeniedException("You can only cancel your own orders");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            return false; // Only allow cancelling if still PLACED
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return true;
    }


    public void updateOrderStatus(UUID orderId, OrderStatus newStatus, String wholesalerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean allItemsBelongToWholesaler = order.getItems().stream()
                .allMatch(item -> item.getProduct().getWholesaler().getEmail().equalsIgnoreCase(wholesalerEmail));

        if (!allItemsBelongToWholesaler) {
            throw new AccessDeniedException("You can only update status for your own orders");
        }

        // Only allow status transitions: PLACED -> SHIPPED -> DELIVERED
        if (newStatus == OrderStatus.SHIPPED && order.getStatus() == OrderStatus.PLACED) {
            order.setStatus(OrderStatus.SHIPPED);
        } else if (newStatus == OrderStatus.DELIVERED && order.getStatus() == OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            throw new IllegalArgumentException("Invalid status transition");
        }

        orderRepository.save(order);
    }


}

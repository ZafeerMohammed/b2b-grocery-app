package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.exception.InsufficientStockException;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.CartItemRepository;
import com.b2bapp.grocery.repository.OrderRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
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
    public List<Order> getOrders(String retailerEmail) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));
        return orderRepository.findByRetailer(retailer);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByWholesalerEmail(String wholesalerEmail) {
        return orderRepository.findOrdersByWholesalerEmail(wholesalerEmail);
    }


    @Override
    public List<ProductSalesStatsDTO> getWholesalerProductStats(String email, String category, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findOrdersByWholesalerEmail(email);

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
        List<Order> orders = orderRepository.findOrdersByWholesalerEmail(email);

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


}

package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.CartItemRepository;
import com.b2bapp.grocery.repository.OrderRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
}

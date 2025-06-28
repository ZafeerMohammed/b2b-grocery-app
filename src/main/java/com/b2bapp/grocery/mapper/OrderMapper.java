package com.b2bapp.grocery.mapper;

import com.b2bapp.grocery.dto.OrderItemResponseDTO;
import com.b2bapp.grocery.dto.OrderResponseDTO;
import com.b2bapp.grocery.model.CartItem;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.OrderItem;
import com.b2bapp.grocery.model.Product;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItemResponseDTO toItemDTO(CartItem item) {
        Product p = item.getProduct();
        return OrderItemResponseDTO.builder()
                .productId(p.getId())
                .productName(p.getName())
                .imageUrls(p.getImageUrls())
                .brand(p.getBrand())
                .unitPrice(p.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getQuantity() * p.getPrice())
                .build();
    }

    public static OrderItemResponseDTO toItemDTO(OrderItem item) {
        Product p = item.getProduct();
        return OrderItemResponseDTO.builder()
                .productId(p.getId())
                .productName(p.getName())
                .imageUrls(p.getImageUrls())
                .brand(p.getBrand())
                .unitPrice(p.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getQuantity() * p.getPrice())
                .category(p.getCategory())
                .unitType(p.getUnitType())
                .build();
    }


    public static OrderResponseDTO toOrderDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .retailerEmail(order.getRetailer().getEmail())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(OrderMapper::toItemDTO)
                        .collect(Collectors.toList()))
                .build();
    }

}

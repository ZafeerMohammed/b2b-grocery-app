package com.b2bapp.grocery.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private UUID productId;
    private String productName;
    private List<String> imageUrls;
    private String brand;
    private double unitPrice;
    private int quantity;
    private double totalPrice;
    private String category;
    private String unitType;



}

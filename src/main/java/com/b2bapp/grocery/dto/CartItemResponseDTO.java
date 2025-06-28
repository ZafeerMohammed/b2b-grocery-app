package com.b2bapp.grocery.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {
    private UUID cartItemId;
    private UUID productId;
    private String productName;
    private List<String> imageUrls;
    private String brand;
    private double price;
    private int quantity; // quantity in cart
}

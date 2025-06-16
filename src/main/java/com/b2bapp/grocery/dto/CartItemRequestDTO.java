package com.b2bapp.grocery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItemRequestDTO {
    private String retailerEmail;
    private UUID productId;
    private int quantity;
}

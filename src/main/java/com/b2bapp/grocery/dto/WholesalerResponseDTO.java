package com.b2bapp.grocery.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WholesalerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private List<ProductDTO> products;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDTO {
        private UUID id;
        private String name;
        private String description;
        private double price;
        private int quantity;
        private String category;
    }
}

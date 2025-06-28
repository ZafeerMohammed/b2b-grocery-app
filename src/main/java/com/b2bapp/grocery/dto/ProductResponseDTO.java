package com.b2bapp.grocery.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private List<String> imageUrls;
    private String brand;
    private String tags;
    private String unitType;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

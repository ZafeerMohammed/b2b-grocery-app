package com.b2bapp.grocery.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    private double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Brand is required")
    private String brand;

    private String tags;

    private String unitType;

    @NotNull(message = "At least one image URL is required")
    @Size(min = 1, max = 5, message = "Product must have between 1 and 5 image URLs")
    private List<@NotBlank String> imageUrls;
}

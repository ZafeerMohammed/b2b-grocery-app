package com.b2bapp.grocery.mapper;

import com.b2bapp.grocery.dto.ProductRequestDTO;
import com.b2bapp.grocery.dto.ProductResponseDTO;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;

import java.util.UUID;

public class ProductMapper {

    // Convert Request DTO to Entity (for create)
    public static Product toEntity(ProductRequestDTO dto, User wholesaler) {
        return Product.builder()
//                .id(UUID.randomUUID())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .category(dto.getCategory())
                .imageUrls(dto.getImageUrls())
                .brand(dto.getBrand())
                .tags(dto.getTags())
                .unitType(dto.getUnitType())
                .wholesaler(wholesaler)
                .active(true)
                .build();
    }

    // Convert Entity to Response DTO
    public static ProductResponseDTO toDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .imageUrls(product.getImageUrls())
                .brand(product.getBrand())
                .tags(product.getTags())
                .unitType(product.getUnitType())
                .active(product.isActive())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .build();
    }

    // Optional: Update entity from DTO
    public static void updateEntity(Product product, ProductRequestDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        product.setImageUrls(dto.getImageUrls());
        product.setBrand(dto.getBrand());
        product.setTags(dto.getTags());
        product.setUnitType(dto.getUnitType());
    }
}

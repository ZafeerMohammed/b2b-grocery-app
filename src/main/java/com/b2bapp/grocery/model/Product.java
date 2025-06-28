package com.b2bapp.grocery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    private double price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "wholesaler_id", nullable = false)
    private User wholesaler;

    private String category;

//    @ElementCollection(fetch = FetchType.EAGER)
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", nullable = false, length = 500)
    @Size(min = 1, max = 5, message = "Product must have between 1 and 5 images")
    private List<String> imageUrls = new ArrayList<>();


    private String brand;

    @Column(length = 200)
    private String tags; // Comma-separated tags

    private String unitType; // e.g., kg, liter, pack

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;



    @PrePersist
    public void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

}

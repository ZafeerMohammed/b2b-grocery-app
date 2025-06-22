package com.b2bapp.grocery.model;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(length = 500)
    private String imageUrl;

    private String brand;

    @Column(length = 200)
    private String tags; // Comma-separated tags

    private String unitType; // e.g., kg, liter, pack

    @Column(nullable = false)
    private boolean active = true;


}

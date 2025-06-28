package com.b2bapp.grocery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private UUID id;

    @ManyToOne(optional = false)
    private Product product;

    private int quantity;

    private double priceAtPurchase; // in case product price changes later

    @ManyToOne(optional = false)
    @JsonBackReference
    private Order order;
}

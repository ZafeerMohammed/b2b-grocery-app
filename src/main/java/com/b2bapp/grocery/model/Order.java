package com.b2bapp.grocery.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private User retailer;  // Who placed the order

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> items;  // What products and how much

    private LocalDateTime orderDate;

    private double totalAmount; // Sum of (price × quantity) for all items

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // e.g., PLACED, SHIPPED, DELIVERED, CANCELLED

    @Column(nullable = false)
    private boolean active = true;

}

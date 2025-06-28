package com.b2bapp.grocery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;


    @ManyToOne(optional = false)
    @JoinColumn(name = "retailer_id")
    private User retailer;

    private int quantity; // Quantity being returned

    private String reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status; // REQUESTED, APPROVED, REJECTED, PROCESSED

    private LocalDateTime requestDate;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void onCreate() {
        this.requestDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = ReturnStatus.REQUESTED;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}

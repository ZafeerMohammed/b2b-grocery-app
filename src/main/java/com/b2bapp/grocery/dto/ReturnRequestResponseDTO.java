package com.b2bapp.grocery.dto;

import com.b2bapp.grocery.model.ReturnStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestResponseDTO {

    private UUID id;
    private UUID orderId;
    private String productName;
    private int quantity;
    private String retailerEmail;
    private String reason;
    private ReturnStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime lastUpdated;
}

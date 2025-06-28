package com.b2bapp.grocery.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestDTO {

    @NotNull(message = "Order ID is required")
    private UUID orderItemId;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    @NotBlank(message = "Reason for return is required")
    private String reason;
}

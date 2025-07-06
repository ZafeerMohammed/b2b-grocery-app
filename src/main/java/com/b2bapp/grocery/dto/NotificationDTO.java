package com.b2bapp.grocery.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationDTO {
    private UUID id;
    private String message;
    private boolean seen;
    private LocalDateTime timestamp;
}

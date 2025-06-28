package com.b2bapp.grocery.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReturnChatMessageDTO {
    private UUID id;
    private UUID returnRequestId;
    private String senderName;
    private String senderEmail;
    private String message;
    private LocalDateTime timestamp;
}




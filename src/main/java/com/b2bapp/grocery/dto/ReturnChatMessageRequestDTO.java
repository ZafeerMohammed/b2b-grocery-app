package com.b2bapp.grocery.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
public class ReturnChatMessageRequestDTO {
    private UUID returnRequestId;
    private String message;
}



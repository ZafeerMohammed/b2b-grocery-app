package com.b2bapp.grocery.mapper;

import com.b2bapp.grocery.dto.ReturnChatMessageDTO;
import com.b2bapp.grocery.model.ReturnChatMessage;

public class ReturnChatMessageMapper {
    public static ReturnChatMessageDTO toDTO(ReturnChatMessage entity) {
        return ReturnChatMessageDTO.builder()
                .id(entity.getId())
                .returnRequestId(entity.getReturnRequest().getId())
                .senderName(entity.getSender().getName())
                .senderEmail(entity.getSender().getEmail())
                .message(entity.getMessage())
                .timestamp(entity.getTimestamp())
                .build();
    }
}


package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.ReturnChatMessageDTO;
import com.b2bapp.grocery.dto.ReturnChatMessageRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReturnChatService {
    void sendMessage(String senderEmail, ReturnChatMessageRequestDTO dto);
    List<ReturnChatMessageDTO> getMessages(UUID returnRequestId);
}

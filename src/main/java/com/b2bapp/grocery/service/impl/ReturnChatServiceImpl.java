package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.ReturnChatMessageDTO;
import com.b2bapp.grocery.dto.ReturnChatMessageRequestDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.mapper.ReturnChatMessageMapper;
import com.b2bapp.grocery.model.ReturnChatMessage;
import com.b2bapp.grocery.model.ReturnRequest;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ReturnChatMessageRepository;
import com.b2bapp.grocery.repository.ReturnRequestRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.ReturnChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnChatServiceImpl implements ReturnChatService {

    private final ReturnChatMessageRepository chatRepo;
    private final ReturnRequestRepository returnRepo;
    private final UserRepository userRepo;

    @Override
    public void sendMessage(String senderEmail, ReturnChatMessageRequestDTO dto) {
        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        ReturnRequest returnRequest = returnRepo.findById(dto.getReturnRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found"));

        ReturnChatMessage message = ReturnChatMessage.builder()
                .sender(sender)
                .returnRequest(returnRequest)
                .message(dto.getMessage())
                .build();

        chatRepo.save(message);
    }

    @Override
    public List<ReturnChatMessageDTO> getMessages(UUID returnRequestId) {
        return chatRepo.findByReturnRequest_IdOrderByTimestampAsc(returnRequestId)
                .stream()
                .map(ReturnChatMessageMapper::toDTO)
                .toList();
    }
}


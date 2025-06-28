package com.b2bapp.grocery.controller;


import com.b2bapp.grocery.dto.*;

import com.b2bapp.grocery.service.ReturnChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/retailer/return/chat")
@RequiredArgsConstructor
public class RetailerReturnChatController {

    private final ReturnChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(
            Principal principal,
            @RequestBody ReturnChatMessageRequestDTO dto
    ) {
        chatService.sendMessage(principal.getName(), dto); // Retailer email
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{returnRequestId}")
    public ResponseEntity<List<ReturnChatMessageDTO>> getMessages(
            @PathVariable UUID returnRequestId
    ) {
        return ResponseEntity.ok(chatService.getMessages(returnRequestId));
    }
}

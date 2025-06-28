package com.b2bapp.grocery.controller;


import com.b2bapp.grocery.dto.ReturnChatMessageDTO;
import com.b2bapp.grocery.dto.ReturnChatMessageRequestDTO;
import com.b2bapp.grocery.service.ReturnChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wholesaler/return/chat")
@RequiredArgsConstructor
public class WholesalerReturnChatController {

    private final ReturnChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(
            Principal principal,
            @RequestBody ReturnChatMessageRequestDTO dto
    ) {
        chatService.sendMessage(principal.getName(), dto); // Wholesaler email
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{returnRequestId}")
    public ResponseEntity<List<ReturnChatMessageDTO>> getMessages(
            @PathVariable UUID returnRequestId
    ) {
        return ResponseEntity.ok(chatService.getMessages(returnRequestId));
    }
}

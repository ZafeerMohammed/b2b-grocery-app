package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.ChatMessage;
import com.b2bapp.grocery.model.Message;
import com.b2bapp.grocery.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat") // Client sends to /app/chat
    @SendTo("/topic/messages")            // Server broadcasts here
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // Save to DB
        Message saved = messageService.saveMessage(
                chatMessage.getSender(),
                chatMessage.getReceiver(),
                chatMessage.getContent()
        );

        // Send to receiver only
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),          // receiver's identifier (e.g., email)
                "/queue/messages",                  // destination the receiver subscribes to
                chatMessage                         // actual message payload
        );
    }
}

package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.Message;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.MessageRepository;
import com.b2bapp.grocery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message saveMessage(String senderEmail, String receiverEmail, String content) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetween(String email1, String email2) {
        User user1 = userRepository.findByEmail(email1)
                .orElseThrow(() -> new RuntimeException("User1 not found"));
        User user2 = userRepository.findByEmail(email2)
                .orElseThrow(() -> new RuntimeException("User2 not found"));

        return messageRepository.findBySenderAndReceiver(user1, user2);
    }

    public List<Message> getAllMessagesForUser(UUID userId) {
        return messageRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}

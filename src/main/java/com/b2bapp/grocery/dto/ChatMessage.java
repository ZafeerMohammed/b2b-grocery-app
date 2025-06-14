package com.b2bapp.grocery.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String sender;    // Email or name
    private String receiver;  // Email or name
    private String content;   // Message text
    private String timestamp; // for display
}

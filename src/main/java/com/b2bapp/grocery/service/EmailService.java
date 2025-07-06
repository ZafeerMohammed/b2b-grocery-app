package com.b2bapp.grocery.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentBytes, String attachmentName);

}

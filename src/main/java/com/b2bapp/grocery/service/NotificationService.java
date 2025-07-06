package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.NotificationDTO;
import com.b2bapp.grocery.model.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NotificationService {

    void notifyUser(User recipient, String messageHeader, String message);

    Page<NotificationDTO> getUserNotifications(String userEmail, int page, int size);

    void markAsSeen(UUID notificationId, String email);

    void markAllAsSeen(String email);

}

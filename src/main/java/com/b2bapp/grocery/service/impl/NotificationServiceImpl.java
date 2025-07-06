package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.NotificationDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.mapper.NotificationMapper;
import com.b2bapp.grocery.model.Notification;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.NotificationRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    @Override
    public void notifyUser(User recipient, String messageHeader, String message) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .timestamp(LocalDateTime.now())
                .seen(false)
                .build();

        notificationRepo.save(notification);
    }

    @Override
    public Page<NotificationDTO> getUserNotifications(String userEmail, int page, int size) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return notificationRepo.findByRecipient(user, pageable)
                .map(NotificationMapper::toDTO);
    }

    @Override
    public void markAsSeen(UUID notificationId, String email) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getEmail().equals(email)) {
            throw new AccessDeniedException("You cannot mark someone else’s notification");
        }
        notification.setSeen(true);
        notificationRepo.save(notification);
    }


    @Override
    public void markAllAsSeen(String email) {
        List<Notification> notifications = notificationRepo.findByRecipientEmailOrderByTimestampDesc(email, Pageable.unpaged())
                .getContent();

        notifications.forEach(n -> n.setSeen(true));
        notificationRepo.saveAll(notifications);
    }

    private NotificationDTO toDTO(Notification notif) {
        return NotificationDTO.builder()
                .id(notif.getId())
                .message(notif.getMessage())
                .seen(notif.isSeen())
                .timestamp(notif.getTimestamp())
                .build();
    }
}

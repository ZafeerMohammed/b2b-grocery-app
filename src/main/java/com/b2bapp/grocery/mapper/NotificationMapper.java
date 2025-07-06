package com.b2bapp.grocery.mapper;

import com.b2bapp.grocery.dto.NotificationDTO;
import com.b2bapp.grocery.model.Notification;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .seen(notification.isSeen())
                .timestamp(notification.getTimestamp())
                .build();
    }
}

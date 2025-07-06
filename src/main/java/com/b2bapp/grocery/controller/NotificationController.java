package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.NotificationDTO;
import com.b2bapp.grocery.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(principal.getName(), page, size);
        return ResponseEntity.ok(notifications);
    }

    // PUT: mark a specific notification as seen
    @PutMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable UUID id, Principal principal) {
        notificationService.markAsSeen(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    // PUT: mark all notifications as seen for the current user
    @PutMapping("/seen-all")
    public ResponseEntity<Void> markAllAsSeen(Principal principal) {
        notificationService.markAllAsSeen(principal.getName());
        return ResponseEntity.ok().build();
    }

}

package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Notification;
import com.b2bapp.grocery.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipient(User recipient, Pageable pageable);

    long countByRecipientAndSeenFalse(User recipient);

    Page<Notification> findByRecipientEmailOrderByTimestampDesc(String email, Pageable pageable);

}

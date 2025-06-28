package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.ReturnChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReturnChatMessageRepository extends JpaRepository<ReturnChatMessage, UUID> {
    List<ReturnChatMessage> findByReturnRequest_IdOrderByTimestampAsc(UUID returnRequestId);
}


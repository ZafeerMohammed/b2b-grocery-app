package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Message;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderAndReceiver(User sender, User receiver);

    List<Message> findBySenderIdOrReceiverId(UUID senderId, UUID receiverId);

}

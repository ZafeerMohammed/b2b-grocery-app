package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);


}

package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Role;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}

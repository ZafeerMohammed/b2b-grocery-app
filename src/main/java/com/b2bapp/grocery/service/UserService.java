package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.Role;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public User registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(user.getRole() != null ? user.getRole() : Role.RETAILER);

        return userRepository.save(user);

    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

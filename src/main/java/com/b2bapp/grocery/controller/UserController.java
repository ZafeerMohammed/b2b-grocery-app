package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {

        User savedUser = userService.registerUser(user);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

    }


    @GetMapping("email/{email}")
    public ResponseEntity<User> registerUser(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();

    }



}

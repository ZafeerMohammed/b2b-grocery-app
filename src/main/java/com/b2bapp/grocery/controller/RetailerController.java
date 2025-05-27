package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.LoginRequest;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.security.JwtService;
import com.b2bapp.grocery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
public class RetailerController {


    @GetMapping("/dashboard")
    public ResponseEntity<String> retailerDashboard() {
        return ResponseEntity.ok("welcome! Retailer");
    }


}

package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.WholesalerResponseDTO;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 1. View all products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    // 2. Delete product by ID
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable UUID productId) {
        adminService.deleteProductById(productId);
        return ResponseEntity.ok().build();
    }

    // 3. Delete products by category
    @DeleteMapping("/products/category")
    public ResponseEntity<Void> deleteProductsByCategory(@RequestParam String category) {
        adminService.deleteProductsByCategory(category);
        return ResponseEntity.ok().build();
    }

    // 4. Delete products by wholesaler email
    @DeleteMapping("/products/wholesaler")
    public ResponseEntity<Void> deleteProductsByWholesaler(@RequestParam String email) {
        adminService.deleteProductsByWholesaler(email);
        return ResponseEntity.ok().build();
    }

    // 5. Get all wholesalers
    @GetMapping("/wholesalers")
    public ResponseEntity<List<WholesalerResponseDTO>> getAllWholesalers() {
        List<WholesalerResponseDTO> dtoList = adminService.getAllWholesalers();

//        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dtoList));

        return ResponseEntity.ok(dtoList);
    }




    // 6. Delete a wholesaler
    @DeleteMapping("/wholesalers")
    public ResponseEntity<Void> deleteWholesaler(@RequestParam String email) {
        adminService.deleteWholesaler(email);
        return ResponseEntity.ok().build();
    }




}

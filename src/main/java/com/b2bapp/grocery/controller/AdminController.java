package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.*;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.ReturnStatus;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.service.AdminService;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ReturnRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final OrderService orderService;

    private final ReturnRequestService returnService;


    // 1. View all products
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDTO> products = adminService.getAllProducts(page, size, sortBy, sortDir);
        return ResponseEntity.ok(products);
    }





    // 2. Delete product by ID
    @DeleteMapping("/products/delete/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable UUID productId) {
        adminService.deleteProductById(productId);
        return ResponseEntity.ok().build();
    }

    // 3. Delete products by category
    @DeleteMapping("/products/delete/category")
    public ResponseEntity<Void> deleteProductsByCategory(@RequestParam String category) {
        adminService.deleteProductsByCategory(category);
        return ResponseEntity.ok().build();
    }

    // 4. Delete products by wholesaler email
    @DeleteMapping("/products/delete/wholesaler")
    public ResponseEntity<Void> deleteProductsByWholesaler(@RequestParam String email) {
        adminService.deleteProductsByWholesaler(email);
        return ResponseEntity.ok().build();
    }


    // 5. Get all wholesalers
    @GetMapping("/wholesalers")
    public ResponseEntity<List<WholesalerResponseDTO>> getAllWholesalers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        List<WholesalerResponseDTO> pagedSortedList = adminService.getAllWholesalers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(pagedSortedList);
    }


    // 6. Delete a wholesaler
    @DeleteMapping("/wholesalers/delete")
    public ResponseEntity<Void> deleteWholesaler(@RequestParam String email) {
        adminService.deleteWholesaler(email);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/returns")
    public ResponseEntity<Page<ReturnRequestResponseDTO>> getAllReturns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(returnService.getAllReturns(page, size));
    }

    @GetMapping("/returns/status")
    public ResponseEntity<Page<ReturnRequestResponseDTO>> getReturnsByStatus(
            @RequestParam ReturnStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(returnService.getReturnsByStatus(status, page, size));
    }

    @PutMapping("/returns/{id}/status")
    public ResponseEntity<Void> updateReturnStatus(
            @PathVariable UUID id,
            @RequestParam ReturnStatus newStatus) {
        returnService.updateReturnStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }

}

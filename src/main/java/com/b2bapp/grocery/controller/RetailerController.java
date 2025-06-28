package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.*;
import com.b2bapp.grocery.mapper.OrderMapper;
import com.b2bapp.grocery.model.CartItem;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.CartService;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ProductService;
import com.b2bapp.grocery.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
public class RetailerController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ReturnRequestService returnService;



    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> orders = orderService.getOrders(principal.getName(), page, size);
        Page<OrderResponseDTO> response = orders.map(OrderMapper::toOrderDTO);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/orders/filter")
    public ResponseEntity<Page<OrderResponseDTO>> filterRetailerOrders(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> orders = orderService.filterRetailerOrders(principal.getName(), category, startDate, endDate, page, size);
        Page<OrderResponseDTO> response = orders.map(OrderMapper::toOrderDTO);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDTO> results = productService.searchProducts(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }


    @PatchMapping("/orders/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable UUID orderId, Principal principal) {
        boolean cancelled = orderService.cancelOrderIfPossible(orderId, principal.getName());
        return ResponseEntity.ok(cancelled ? "Order cancelled successfully" : "Order cannot be cancelled");
    }




    // Returns
    @PostMapping("/returns")
    public ResponseEntity<ReturnRequestResponseDTO> createReturnRequest(
            Principal principal,
            @RequestBody ReturnRequestDTO dto) {
        return ResponseEntity.ok(returnService.createReturnRequest(principal.getName(), dto));
    }

    @GetMapping("/returns")
    public ResponseEntity<Page<ReturnRequestResponseDTO>> getMyReturns(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(returnService.getReturnsForRetailer(principal.getName(), page, size));
    }


}

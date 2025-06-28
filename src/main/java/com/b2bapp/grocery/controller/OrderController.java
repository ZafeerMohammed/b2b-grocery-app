package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.OrderResponseDTO;
import com.b2bapp.grocery.dto.TotalSalesStatsDTO;
import com.b2bapp.grocery.mapper.OrderMapper;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Get all orders (no pagination)
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> orders = orderService.getAllOrders(page, size);
        Page<OrderResponseDTO> response = orders.map(OrderMapper::toOrderDTO);
        return ResponseEntity.ok(response);
    }


    // Get paginated orders by retailer
    @GetMapping("/orders/by-retailer")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByRetailer(
            @RequestParam String retailerEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> orders = orderService.getOrders(retailerEmail, page, size);
        Page<OrderResponseDTO> response = orders.map(OrderMapper::toOrderDTO);
        return ResponseEntity.ok(response);
    }


    // Paginated filter for Admin
    @GetMapping("/orders/filter")
    public ResponseEntity<Page<OrderResponseDTO>> filterOrders(
            @RequestParam(required = false) String retailerEmail,
            @RequestParam(required = false) String wholesalerEmail,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> orders = orderService.filterOrdersForAdmin(
                retailerEmail, wholesalerEmail, category, startDate, endDate, page, size
        );
        Page<OrderResponseDTO> response = orders.map(OrderMapper::toOrderDTO);
        return ResponseEntity.ok(response);
    }

}

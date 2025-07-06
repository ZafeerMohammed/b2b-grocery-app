package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.OrderResponseDTO;
import com.b2bapp.grocery.dto.ProductResponseDTO;
import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.dto.ReturnRequestResponseDTO;
import com.b2bapp.grocery.mapper.OrderMapper;
import com.b2bapp.grocery.mapper.ProductMapper;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.OrderStatus;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.ReturnStatus;
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
@RequestMapping("/api/wholesaler")
@RequiredArgsConstructor
public class WholesalerController {

    private final ProductService productService;

    private final OrderService orderService;

    private final ReturnRequestService returnService;




    //  Get products owned by logged-in wholesaler
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponseDTO>> getOwnProducts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDTO> products = productService
                .getByWholesaler(principal.getName(), page, size, sortBy, sortDir)
                .map(ProductMapper::toDTO);

        return ResponseEntity.ok(products);
    }






    // 2. Get products by category for wholesaler
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Page<ProductResponseDTO>> getByCategoryForWholesaler(
            @PathVariable String category,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDTO> products = productService
                .getByCategoryAndWholesaler(category, principal.getName(), page, size, sortBy, sortDir)
                .map(ProductMapper::toDTO);

        return ResponseEntity.ok(products);
    }






    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByWholesaler(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByWholesalerEmail(principal.getName(), page, size)
                        .map(OrderMapper::toOrderDTO)
        );
    }



    @GetMapping("/orders/filter")
    public ResponseEntity<Page<OrderResponseDTO>> filterWholesalerOrders(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService
                .filterWholesalerOrders(principal.getName(), category, startDate, endDate, page, size)
                .map(OrderMapper::toOrderDTO));
    }




    @PatchMapping("/orders/update-status")
    public ResponseEntity<String> updateOrderStatus(
            @RequestParam UUID orderId,
            @RequestParam OrderStatus status,
            Principal principal
    ) {
        orderService.updateOrderStatus(orderId, status, principal.getName());
        return ResponseEntity.ok("Order status updated to " + status);
    }

    @GetMapping("/returns")
    public ResponseEntity<Page<ReturnRequestResponseDTO>> getReturnsForMyProducts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(returnService.getReturnsForWholesaler(principal.getName(), page, size));
    }

    @PutMapping("/returns/{id}/status")
    public ResponseEntity<Void> updateReturnStatus(
            Principal principal,
            @PathVariable UUID id,
            @RequestParam ReturnStatus newStatus) {
        returnService.updateReturnStatusByWholesaler(id, newStatus, principal.getName());
        return ResponseEntity.ok().build();
    }


}

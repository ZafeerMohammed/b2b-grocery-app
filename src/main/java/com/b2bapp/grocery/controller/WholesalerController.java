package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ProductService;
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



    //  Get products owned by logged-in wholesaler
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getOwnProducts(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getByWholesaler(principal.getName(), page, size));
    }





    //  Get products owned by logged-in wholesaler by Catagory
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Page<Product>> getByCategoryForWholesaler(
            @PathVariable String category,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getByCategoryAndWholesaler(category, principal.getName(), page, size));
    }





    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getOrdersByWholesaler(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getOrdersByWholesalerEmail(principal.getName(), page, size));
    }


    @GetMapping("/orders/filter")
    public ResponseEntity<Page<Order>> filterWholesalerOrders(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.filterWholesalerOrders(principal.getName(), category, startDate, endDate, page, size));
    }



    @GetMapping("/orders/stats")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductSalesStats(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getWholesalerProductStats(principal.getName(), category, startDate, endDate));
    }


    @GetMapping("/orders/top-selling")
    public ResponseEntity<List<ProductSalesStatsDTO>> getTopSellingProducts(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getTopSellingProducts(principal.getName(), category, startDate, endDate));
    }

}

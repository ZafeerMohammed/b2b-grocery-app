package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wholesaler")
@RequiredArgsConstructor
public class WholesalerController {

    private final ProductService productService;

    private final OrderService orderService;


    // ✅ 1. Add product
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product,
                                              @RequestParam String wholesalerEmail) {
        return ResponseEntity.ok(productService.addProduct(product, wholesalerEmail));
    }

    // ✅ 2. View all products
    @GetMapping("/products/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ 3. View products of this wholesaler
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getByWholesaler(@RequestParam String email) {
        return ResponseEntity.ok(productService.getByWholesaler(email));
    }

    // ✅ 4. Delete own product
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId,
                                                @RequestParam String wholesalerEmail) {
        productService.deleteProductByWholesaler(productId, wholesalerEmail);
        return ResponseEntity.ok("Product deleted");
    }


    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersByWholesaler(@RequestParam String email) {
        return ResponseEntity.ok(orderService.getOrdersByWholesalerEmail(email));
    }

    @GetMapping("/orders/stats")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductSalesStats(
            @RequestParam String email,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getWholesalerProductStats(email, category, startDate, endDate));
    }


    @GetMapping("/orders/top-selling")
    public ResponseEntity<List<ProductSalesStatsDTO>> getTopSellingProducts(
            @RequestParam String email,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getTopSellingProducts(email, category, startDate, endDate));
    }

}

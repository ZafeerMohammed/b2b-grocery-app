package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product,
                                              @RequestParam String wholesalerEmail) {
        return ResponseEntity.ok(productService.addProduct(product, wholesalerEmail));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    @GetMapping("/wholesaler")
    public ResponseEntity<List<Product>> getByWholesaler(@RequestParam String email) {
        return ResponseEntity.ok(productService.getByWholesaler(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable UUID id,
                                          @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        productService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
}

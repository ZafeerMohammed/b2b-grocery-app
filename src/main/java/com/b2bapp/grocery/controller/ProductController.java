package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wholesaler/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;



    // 1. Add product
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product,
                                              Principal principal) {
        return ResponseEntity.ok(productService.addProduct(product, principal.getName()));
    }




    // 2. Update product
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> update(@PathVariable UUID id,
                                          @RequestBody Product product,
                                          Principal principal) {
        return ResponseEntity.ok(productService.updateProduct(id, product, principal.getName()));
    }


    // 3. Delete own product
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId,
                                                Principal principal) {
        productService.softDeleteProductByWholesaler(productId, principal.getName());
        return ResponseEntity.ok("Product deleted");
    }


    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }


}

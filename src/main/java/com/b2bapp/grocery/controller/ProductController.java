package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.ProductRequestDTO;
import com.b2bapp.grocery.dto.ProductResponseDTO;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.ProductService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody @Valid ProductRequestDTO productDTO,
                                                         Principal principal) {
        return ResponseEntity.ok(productService.createProduct(productDTO, principal.getName()));
    }





    // 2. Update product
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable UUID id,
                                                     @RequestBody @Valid ProductRequestDTO productDTO,
                                                     Principal principal) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, principal.getName()));
    }


    // 3. Delete own product
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId,
                                                Principal principal) {
        productService.softDeleteProductByWholesaler(productId, principal.getName());
        return ResponseEntity.ok("Product deleted");
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDTO> results = productService.searchProductsOfWholesaler(principal.getName(), keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(results);
    }


}

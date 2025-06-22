package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ProductRepository;
import com.b2bapp.grocery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Product addProduct(Product product, String wholesalerEmail) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        product.setWholesaler(wholesaler);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getByCategoryAndWholesaler(String category, String wholesalerEmail, int page, int size) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndWholesaler(category, wholesaler, pageable);
    }

    public Page<Product> getByWholesaler(String wholesalerEmail, int page, int size) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByWholesaler(wholesaler, pageable);
    }

    public Product updateProduct(UUID id, Product newProduct, String wholesalerEmail) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        if (!existing.getWholesaler().getId().equals(wholesaler.getId())) {
            throw new RuntimeException("You can only update your own products");
        }

        existing.setName(newProduct.getName());
        existing.setDescription(newProduct.getDescription());
        existing.setPrice(newProduct.getPrice());
        existing.setQuantity(newProduct.getQuantity());
        existing.setCategory(newProduct.getCategory());

        // Newly added
        existing.setImageUrl(newProduct.getImageUrl());
        existing.setBrand(newProduct.getBrand());
        existing.setTags(newProduct.getTags());
        existing.setUnitType(newProduct.getUnitType());

        return productRepository.save(existing);
    }



    public void softDeleteProductByWholesaler(UUID productId, String wholesalerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        // Check ownership
        if (!product.getWholesaler().getId().equals(wholesaler.getId())) {
            throw new AccessDeniedException("Unauthorized : You can only delete your own products");
        }

        product.setActive(false);
        productRepository.save(product);
    }



    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchByKeyword(keyword, pageable);
    }

}

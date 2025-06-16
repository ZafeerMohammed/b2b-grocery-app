package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.WholesalerResponseDTO;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.Role;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ProductRepository;
import com.b2bapp.grocery.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 1. Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. Delete product by ID
    public void deleteProductById(UUID productId) {
        productRepository.deleteById(productId);
    }

    // 3. Delete products by category
    @Transactional
    public void deleteProductsByCategory(String category) {
        productRepository.deleteByCategoryIgnoreCase(category);
    }

    // 4. Delete products by wholesaler email
    @Transactional
    public void deleteProductsByWholesaler(String email) {
        User wholesaler = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        productRepository.deleteByWholesaler(wholesaler);
    }

    // 5. Get all wholesalers
    public List<WholesalerResponseDTO> getAllWholesalers() {
        return userRepository.findByRole(Role.WHOLESALER)
                .stream()
                .map(user -> WholesalerResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .products(user.getProducts().stream()
                                .map(product -> WholesalerResponseDTO.ProductDTO.builder()
                                        .id(product.getId())
                                        .name(product.getName())
                                        .description(product.getDescription())
                                        .price(product.getPrice())
                                        .quantity(product.getQuantity())
                                        .category(product.getCategory())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }



    // 6. Delete wholesaler account
    public void deleteWholesaler(String email) {
        User wholesaler = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        userRepository.delete(wholesaler);
    }
}

package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.WholesalerResponseDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.Role;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ProductRepository;
import com.b2bapp.grocery.repository.UserRepository;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 1. Get all products
    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAllByActiveTrue(pageable);
    }

    // 2. Delete product by ID
    public void deleteProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        product.setActive(false);
        productRepository.save(product);
    }

    // 3. Delete products by category
    @Transactional
    public void deleteProductsByCategory(String category) {
        productRepository.softDeleteByCategoryIgnoreCase(category);
    }

    // 4. Delete products by wholesaler email
    @Transactional
    public void deleteProductsByWholesaler(String email) {
        User wholesaler = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        productRepository.softDeleteByWholesaler(wholesaler);
    }

    // 5. Get all wholesalers
    public List<WholesalerResponseDTO> getAllWholesalers() {
        return userRepository.findByRole(Role.WHOLESALER)
                .stream()
                .filter(User::isActive)
                .map(user -> WholesalerResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .products(user.getProducts().stream()
                                .filter(Product::isActive)  // Get only active products
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
    @Transactional
    public void deleteWholesaler(String email) {

        User wholesaler = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        wholesaler.setActive(false);
        userRepository.save(wholesaler);

        productRepository.deactivateProductsByWholesaler(wholesaler);

    }
}

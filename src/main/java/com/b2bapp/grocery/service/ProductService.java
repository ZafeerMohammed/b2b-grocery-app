package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.LowStockProductDTO;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ProductRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.b2bapp.grocery.dto.ProductRequestDTO;
import com.b2bapp.grocery.dto.ProductResponseDTO;
import com.b2bapp.grocery.mapper.ProductMapper;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public ProductResponseDTO createProduct(ProductRequestDTO dto, String wholesalerEmail) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        if (dto.getImageUrls() == null || dto.getImageUrls().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        if (dto.getImageUrls().size() > 5) {
            throw new IllegalArgumentException("Maximum 5 images allowed.");
        }


        Product product = ProductMapper.toEntity(dto, wholesaler);
        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getByCategoryAndWholesaler(String category, String wholesalerEmail, int page, int size, String sortBy, String sortDir) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        Sort sort = SortUtil.getValidatedSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findByCategoryAndWholesaler(category, wholesaler, pageable);
    }

    public Page<Product> getByWholesaler(String wholesalerEmail, int page, int size, String sortBy, String sortDir) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        Sort sort = SortUtil.getValidatedSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findByWholesaler(wholesaler, pageable);
    }



    public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO dto, String wholesalerEmail) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        if (!existing.getWholesaler().getId().equals(wholesaler.getId())) {
            throw new RuntimeException("You can only update your own products");
        }

        ProductMapper.updateEntity(existing, dto);
        Product updated = productRepository.save(existing);
        return ProductMapper.toDTO(updated);
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



    public Page<ProductResponseDTO> searchProductsOfWholesaler(String wholesalerEmail, String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, SortUtil.getValidatedSort(sortBy, sortDir));
        Page<Product> products = productRepository.searchByKeywordForWholesaler(wholesalerEmail, keyword, pageable);
        return products.map(ProductMapper::toDTO);
    }


    public Page<ProductResponseDTO> searchProducts(String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, SortUtil.getValidatedSort(sortBy, sortDir));
        Page<Product> products = productRepository.searchByKeywordForRetailer(keyword, pageable);
        return products.map(ProductMapper::toDTO);
    }



    public List<LowStockProductDTO> getLowStockProductsForWholesaler(String wholesalerEmail) {
        List<Product> products = productRepository.findByWholesaler_Email(wholesalerEmail);

        return products.stream()
                .filter(p -> p.getQuantity() < p.getMinimumStockThreshold())
                .map(p -> new LowStockProductDTO(
                        p.getName(),
                        p.getQuantity(),
                        p.getMinimumStockThreshold(),
                        p.getCategory()
                ))
                .collect(Collectors.toList());
    }



}

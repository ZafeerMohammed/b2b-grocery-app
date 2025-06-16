package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import com.b2bapp.grocery.repository.ProductRepository;
import com.b2bapp.grocery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getByWholesaler(String wholesalerEmail) {
        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));
        return productRepository.findByWholesaler(wholesaler);
    }

    public Product updateProduct(UUID id, Product newProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(newProduct.getName());
        existing.setDescription(newProduct.getDescription());
        existing.setPrice(newProduct.getPrice());
        existing.setQuantity(newProduct.getQuantity());
        existing.setCategory(newProduct.getCategory());
        return productRepository.save(existing);
    }

//    public void deleteProduct(UUID id) {
//        productRepository.deleteById(id);
//    }

    public void deleteProductByWholesaler(UUID productId, String wholesalerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User wholesaler = userRepository.findByEmail(wholesalerEmail)
                .orElseThrow(() -> new RuntimeException("Wholesaler not found"));

        // Check ownership
        if (!product.getWholesaler().getId().equals(wholesaler.getId())) {
            throw new RuntimeException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

}

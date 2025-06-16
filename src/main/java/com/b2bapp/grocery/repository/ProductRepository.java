package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByWholesaler(User wholesaler);

    List<Product> findByCategory(String category);

//    List<Product> findByUser(User user);

    void deleteByCategoryIgnoreCase(String category);

    void deleteByWholesaler(User wholesaler);

}

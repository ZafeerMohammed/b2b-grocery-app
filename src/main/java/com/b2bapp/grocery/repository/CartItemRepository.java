package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.CartItem;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByRetailer(User retailer);
    void deleteByRetailer(User retailer);
}

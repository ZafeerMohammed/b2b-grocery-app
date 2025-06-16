package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByRetailer(User retailer);
}

package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByRetailer(User retailer);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.wholesaler.email = :email")
    List<Order> findOrdersByWholesalerEmail(@Param("email") String wholesalerEmail);

}

package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    Page<Order> findByRetailer(User retailer, Pageable pageable);

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    List<Order> findOrdersByItems_Product_Wholesaler_Email(String email);

//    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.wholesaler.email = :email")
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.product.wholesaler.email = :email")
    Page<Order> findOrdersByWholesalerEmail(@Param("email") String wholesalerEmail, Pageable pageable);




}

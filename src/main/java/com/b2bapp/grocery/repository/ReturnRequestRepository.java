package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.ReturnRequest;
import com.b2bapp.grocery.model.ReturnStatus;
import com.b2bapp.grocery.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {

    // Retailer-specific
    Page<ReturnRequest> findByRetailer(User retailer, Pageable pageable);

    // Status-based
    Page<ReturnRequest> findByStatus(ReturnStatus status, Pageable pageable);

    // Wholesaler-specific: based on the products in order items
    Page<ReturnRequest> findByOrderItem_Product_Wholesaler_Email(String email, Pageable pageable);

    // Admin view (fallback)
    Page<ReturnRequest> findAll(Pageable pageable);

//    Page<ReturnRequest> findByRetailer_Email(String retailerEmail);
}

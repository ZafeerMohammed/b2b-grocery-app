package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.ReturnRequestDTO;
import com.b2bapp.grocery.dto.ReturnRequestResponseDTO;
import com.b2bapp.grocery.model.ReturnStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ReturnRequestService {

    // Retailer actions
    ReturnRequestResponseDTO createReturnRequest(String retailerEmail, ReturnRequestDTO dto);
    Page<ReturnRequestResponseDTO> getReturnsForRetailer(String retailerEmail, int page, int size);

    // Wholesaler actions
    Page<ReturnRequestResponseDTO> getReturnsForWholesaler(String wholesalerEmail, int page, int size);

    // Admin actions
    Page<ReturnRequestResponseDTO> getAllReturns(int page, int size);
    Page<ReturnRequestResponseDTO> getReturnsByStatus(ReturnStatus status, int page, int size);
    void updateReturnStatus(UUID returnId, ReturnStatus newStatus);

    void updateReturnStatusByWholesaler(UUID returnId, ReturnStatus newStatus, String wholesalerEmail);
}

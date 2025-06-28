package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.ReturnRequestDTO;
import com.b2bapp.grocery.dto.ReturnRequestResponseDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.mapper.ReturnRequestMapper;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.OrderItemRepository;
import com.b2bapp.grocery.repository.ReturnRequestRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository orderItemRepo;

    @Override
    public ReturnRequestResponseDTO createReturnRequest(String retailerEmail, ReturnRequestDTO dto) {
        User retailer = userRepo.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        OrderItem orderItem = orderItemRepo.findById(dto.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder().getRetailer().getEmail().equals(retailerEmail)) {
            throw new SecurityException("Cannot return item not owned by this retailer");
        }

        if (dto.getQuantity() > orderItem.getQuantity()) {
            throw new IllegalArgumentException("Cannot return more than purchased");
        }

        ReturnRequest request = ReturnRequest.builder()
                .orderItem(orderItem)
                .retailer(retailer)
                .reason(dto.getReason())
                .quantity(dto.getQuantity())
                .status(ReturnStatus.REQUESTED)
                .requestDate(java.time.LocalDateTime.now())
                .build();

        return ReturnRequestMapper.toDTO(returnRepo.save(request));
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsForRetailer(String retailerEmail, int page, int size) {
        User retailer = userRepo.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByRetailer(retailer, pageable)
                .map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsForWholesaler(String wholesalerEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByOrderItem_Product_Wholesaler_Email(wholesalerEmail, pageable)
                .map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getAllReturns(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findAll(pageable).map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsByStatus(ReturnStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByStatus(status, pageable).map(ReturnRequestMapper::toDTO);
    }

    @Override
    public void updateReturnStatus(UUID returnId, ReturnStatus newStatus) {
        ReturnRequest request = returnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found"));

        request.setStatus(newStatus);
        returnRepo.save(request);
    }

    public void updateReturnStatusByWholesaler(UUID returnId, ReturnStatus newStatus, String wholesalerEmail) {
        ReturnRequest request = returnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found"));

        if (!request.getOrderItem().getProduct().getWholesaler().getEmail().equalsIgnoreCase(wholesalerEmail)) {
            throw new AccessDeniedException("Not authorized to update this return");
        }

        request.setStatus(newStatus);
        returnRepo.save(request);
    }

}

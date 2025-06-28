package com.b2bapp.grocery.mapper;

import com.b2bapp.grocery.dto.ReturnRequestResponseDTO;
import com.b2bapp.grocery.model.ReturnRequest;

public class ReturnRequestMapper {

    public static ReturnRequestResponseDTO toDTO(ReturnRequest request) {
        return ReturnRequestResponseDTO.builder()
                .id(request.getId())
                .orderId(request.getOrderItem().getOrder().getId())
                .productName(request.getOrderItem().getProduct().getName())
                .retailerEmail(request.getRetailer().getEmail())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .status(request.getStatus())
                .requestDate(request.getRequestDate())
                .lastUpdated(request.getLastUpdated())
                .build();
    }


}

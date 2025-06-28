package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopRetailerDTO {
    private String retailerName;
    private String retailerEmail;
    private int totalOrders;
    private double totalSpent;
}

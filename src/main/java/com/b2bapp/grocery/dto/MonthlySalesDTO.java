package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesDTO {
    private String month;       // e.g., "January 2025"
    private long totalOrders;
    private double totalRevenue;
}

package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalSalesStatsDTO {
    private int totalOrders;
    private double totalRevenue;
    private double averageOrderValue;
}

package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesStatsDTO {
    private String productName;
    private int totalUnitsSold;
    private double totalRevenue;
}

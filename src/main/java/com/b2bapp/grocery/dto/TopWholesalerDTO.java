package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopWholesalerDTO {
    private String wholesalerName;
    private String wholesalerEmail;
    private long totalUnitsSold;
    private double totalRevenue;
}

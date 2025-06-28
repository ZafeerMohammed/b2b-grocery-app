package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopRetailerForWholesalerDTO {
    private String retailerName;
    private String retailerEmail;
    private long totalUnitsBought;
    private double totalSpent;
}

package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCategoryDTO {
    private String category;
    private long totalUnitsSold;
    private double totalRevenue;
}

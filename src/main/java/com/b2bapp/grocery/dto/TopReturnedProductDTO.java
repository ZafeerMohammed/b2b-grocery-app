package com.b2bapp.grocery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopReturnedProductDTO {
    private String productName;
    private long totalReturnedUnits;
}

package com.necom.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVariantRequest {
    private Long variantId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}

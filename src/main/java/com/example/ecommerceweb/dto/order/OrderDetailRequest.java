package com.example.ecommerceweb.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    private Long orderId;
    private Long productId;
    private BigDecimal price;
    private Integer numberOfProducts;
    private BigDecimal totalPrice;
}

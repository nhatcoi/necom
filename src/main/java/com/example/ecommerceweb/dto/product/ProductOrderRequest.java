package com.example.ecommerceweb.dto.product;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOrderRequest {
    private Long id;
    private String name;

    private BigDecimal price;
    private Long amount;
    private Integer quantity;
    private String currency;

    private String attributes;


}

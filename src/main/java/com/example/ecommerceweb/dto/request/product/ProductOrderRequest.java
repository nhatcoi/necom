package com.example.ecommerceweb.dto.request.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOrderRequest {
    private Long id;
    private Long amount;
    private Integer quantity;
    private String name;
    private String currency;

}

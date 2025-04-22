package com.example.ecommerceweb.dto.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOrderRequest {
    private Long id;
    private String name;

    private Float price;
    private Long amount;
    private Integer quantity;
    private String currency;

    private String attributes;


}

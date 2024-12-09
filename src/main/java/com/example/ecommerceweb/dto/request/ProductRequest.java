package com.example.ecommerceweb.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}

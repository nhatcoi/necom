package com.example.ecommerceweb.dto.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private List<ProductRequest> products;
}

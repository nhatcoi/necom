package com.example.ecommerceweb.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    private Long orderId;
    private Long productId;
    private Float price;
    private Integer numberOfProducts;
    private Float totalPrice;
}

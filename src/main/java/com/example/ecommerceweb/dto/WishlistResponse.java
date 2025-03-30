package com.example.ecommerceweb.dto;

import com.example.ecommerceweb.dto.response.product.ProductDetailResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;
    private Long productId;
    private String name;
    private Float price;
    private String thumbnail;
}

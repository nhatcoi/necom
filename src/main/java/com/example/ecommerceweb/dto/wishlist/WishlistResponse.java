package com.example.ecommerceweb.dto.wishlist;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;
    private Long productId;
    private String name;
    private BigDecimal price;
    private String thumbnail;
}

package com.example.ecommerceweb.dto.wishlist;

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

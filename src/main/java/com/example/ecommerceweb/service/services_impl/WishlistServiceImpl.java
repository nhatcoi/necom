package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.WishlistResponse;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.entity.Wishlist;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.repository.WishlistRepository;
import com.example.ecommerceweb.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl {

    private final WishlistRepository wishlistRepository;
    private final SecurityUtils securityUtils;

    public List<WishlistResponse> getWishlistByUser() {
        User user = securityUtils.getCurrentUser();
        return wishlistRepository.findByUserId(user.getId()).stream()
                .map(wishlist -> WishlistResponse.builder()
                        .id(wishlist.getId())
                        .productId(wishlist.getProduct().getId())
                        .name(wishlist.getProduct().getName())
                        .price(wishlist.getProduct().getPrice())
                        .thumbnail(wishlist.getProduct().getThumbnail())
                        .build())
                .toList();
    }

    @Transactional
    public void addToWishlist(Long productId) {
        User user = securityUtils.getCurrentUser();
        if (wishlistRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new ResourceException(ErrorCode.DATA_EXISTED);
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(Product.builder().id(productId).build())
                .build();

        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = securityUtils.getCurrentUser();
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }
}

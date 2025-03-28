package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.entity.Wishlist;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    public List<Wishlist> getWishlistByUser(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // Thêm sản phẩm vào wishlist
    @Transactional
    public Wishlist addToWishlist(Long userId, Product product) {
        // Kiểm tra xem sản phẩm đã có trong wishlist chưa
        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserIdAndProductId(userId, product.getId());

        if (existingWishlist.isPresent()) {
            throw new RuntimeException("Sản phẩm đã có trong wishlist!");
        }

        Wishlist wishlist = new Wishlist();

        wishlist.setProduct(product);
        return wishlistRepository.save(wishlist);
    }

    // Xóa sản phẩm khỏi wishlist
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }
}

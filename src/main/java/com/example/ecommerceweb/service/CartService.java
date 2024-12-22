package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public interface CartService {
    List<Cart> getCartItems();
    Integer getTotalInCart();
    void removeItem(Long id);
    void updateCartItem(Long id, Integer quantity);
    Integer createCartItem(Long productId);
    void removeItems(String username, List<Long> productIds);
}

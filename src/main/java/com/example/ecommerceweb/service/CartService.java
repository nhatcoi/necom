package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<Cart> getCartItems();
    Integer getTotalInCart();
    void removeItem(Long id);
    void updateCartItem(Long id, Integer quantity);
    Integer createCartItem(Long productId);
}

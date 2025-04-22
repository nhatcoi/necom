package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.cart.CartRequest;
import com.example.ecommerceweb.dto.cart.CartResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public interface CartService {
    List<CartResponse> getCartItems();
    CartResponse createCartItem(CartRequest cartRequest);
    Integer getTotalInCart();
    void removeItem(Long id);

    void removeItems(String username, List<Long> productIds);
    void updateCartItem(Long id, Integer quantity);

    void clearCart();
}

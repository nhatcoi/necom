package com.example.ecommerceweb.services;

import com.example.ecommerceweb.entities.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartItem> getCartItems(Long cartId);
    Integer getCountInCart(Long cartId);
}

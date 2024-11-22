package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartItem> getCartItems(Long cartId);
    Integer getCountInCart(Long cartId);
}

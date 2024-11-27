package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<Cart> getCartItems(Long cartId);
    Integer getCountInCart();
}

package com.example.ecommerceweb.services.services_impl;

import com.example.ecommerceweb.entities.CartItem;
import com.example.ecommerceweb.repository.CartItemRepository;
import com.example.ecommerceweb.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Override
    public Integer getCountInCart(Long cartId) {
        return cartItemRepository.countProductByCartId(cartId);
    }
}

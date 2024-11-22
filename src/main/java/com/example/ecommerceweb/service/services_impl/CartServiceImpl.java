package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.CartItem;
import com.example.ecommerceweb.repository.CartItemRepository;
import com.example.ecommerceweb.service.CartService;
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

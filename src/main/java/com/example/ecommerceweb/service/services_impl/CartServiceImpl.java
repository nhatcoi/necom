package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.repository.CartRepository;
import com.example.ecommerceweb.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;


    @Override
    public List<Cart> getCartItems(Long userId) {
        var context = SecurityContextHolder.getContext();
        context.getAuthentication().getName();



        return cartRepository.findByUserId(userId);
    }

    @Override
    public Integer getCountInCart() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("User is not authenticated");
            throw new IllegalStateException("User is not authenticated");
        }

        String username = authentication.getName();
        log.info("username: {}", username);

        Integer count = cartRepository.countByUsername(username);
        log.info("count: {}", count);

        return count != null ? count : 0;
    }
}

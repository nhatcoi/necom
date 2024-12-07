package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
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
    public List<Cart> getCartItems() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return cartRepository.findByUserName(username);
    }

    @Override
    public Integer getCountInCart() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String username = authentication.getName();
        Integer count = cartRepository.countByUsername(username);
        return count != null ? count : 0;
    }

    @Override
    public void removeItem(Long id) {
        cartRepository.findById(id).ifPresentOrElse(
                cart -> cartRepository.deleteById(id),
                () -> { throw new ResourceException(ErrorCode.CART_NOT_FOUND); }
        );
    }

    @Override
    public void updateCartItem(Long id, Integer quantity) {
        cartRepository.findById(id).ifPresentOrElse(
                cart -> {
                    cart.setQuantity(quantity);
                    cartRepository.save(cart);
                },
                () -> { throw new ResourceException(ErrorCode.CART_NOT_FOUND); }
        );
    }
}

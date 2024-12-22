package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.CartRepository;
import com.example.ecommerceweb.repository.ProductRepository;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.CartService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Cart> getCartItems() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return cartRepository.findByUserName(username);
    }

    @Override
    public Integer getTotalInCart() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceException(ErrorCode.UNAUTHENTICATED);
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

    @Override
    public Integer createCartItem(Long productId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String username = authentication.getName();



        cartRepository.findByUsernameAndProductId(username, productId).ifPresentOrElse(
                cart -> {
                    if(cart.getQuantity() < 5) cart.setQuantity(cart.getQuantity() + 1);
                    else throw new ResourceException(ErrorCode.CART_QUANTITY_LIMIT);
                    cartRepository.save(cart);
                },
                () -> {
                    var user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));

                    var product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceException(ErrorCode.PRODUCT_NOT_EXISTED));
                    Cart cartItem = Cart.builder()
                            .user(user)
                            .product(product)
                            .quantity(1)
                            .build();
                    cartRepository.save(cartItem);
                }
        );

        return cartRepository.countByUsername(username);
    }

    @Override
    public void removeItems(String username, List<Long> productIds) {
        Long userId = userRepository.findIdByUsername(username);
        cartRepository.deleteByUserIdAndProductId(userId, productIds);
    }
}

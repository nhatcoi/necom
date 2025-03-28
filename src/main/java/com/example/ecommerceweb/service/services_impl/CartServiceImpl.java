//package com.example.ecommerceweb.service.services_impl;
//
//import com.example.ecommerceweb.entity.Cart;
//import com.example.ecommerceweb.exception.ErrorCode;
//import com.example.ecommerceweb.exception.ResourceException;
//import com.example.ecommerceweb.repository.CartRepository;
//import com.example.ecommerceweb.repository.ProductRepository;
//import com.example.ecommerceweb.repository.UserRepository;
//import com.example.ecommerceweb.service.CartService;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CartServiceImpl implements CartService {
//
//    private final CartRepository cartRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<Cart> getCartItems() {
//        String username = getAuthenticatedUsername();
//        return cartRepository.findByUserName(username);
//    }
//
//    @Override
//    public Integer getTotalInCart() {
//        String username = getAuthenticatedUsername();
//        return cartRepository.countByUsername(username) != null ? cartRepository.countByUsername(username) : 0;
//    }
//
//    @Override
//    public void removeItem(Long id) {
//        Cart cart = findCartById(id);
//        cartRepository.delete(cart);
//    }
//
//    @Override
//    public void updateCartItem(Long id, Integer quantity) {
//        Cart cart = findCartById(id);
//        cart.setQuantity(quantity);
//        cartRepository.save(cart);
//    }
//
//    @Override
//    public Integer createCartItem(Long productId) {
//        String username = getAuthenticatedUsername();
//        Cart cart = findCartByUsernameAndProductId(username, productId);
//
//        if (cart != null) {
//            updateCartItemQuantity(cart);
//        } else {
//            createNewCartItem(username, productId);
//        }
//
//        return cartRepository.countByUsername(username) != null ? cartRepository.countByUsername(username) : 0;
//    }
//
//    @Override
//    public void removeItems(String username, List<Long> productIds) {
//        Long userId = userRepository.findIdByUsername(username);
//        cartRepository.deleteByUserIdAndProductId(userId, productIds);
//    }
//
//    private String getAuthenticatedUsername() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ResourceException(ErrorCode.UNAUTHENTICATED);
//        }
//        return authentication.getName();
//    }
//
//    private Cart findCartById(Long id) {
//        return cartRepository.findById(id)
//                .orElseThrow(() -> new ResourceException(ErrorCode.CART_NOT_FOUND));
//    }
//
//    private Cart findCartByUsernameAndProductId(String username, Long productId) {
//        return cartRepository.findByUsernameAndProductId(username, productId)
//                .orElse(null);
//    }
//
//    private void updateCartItemQuantity(Cart cart) {
//        if (cart.getQuantity() < 5) {
//            cart.setQuantity(cart.getQuantity() + 1);
//            cartRepository.save(cart);
//        } else {
//            throw new ResourceException(ErrorCode.CART_QUANTITY_LIMIT);
//        }
//    }
//
//    private void createNewCartItem(String username, Long productId) {
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
//
//        var product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceException(ErrorCode.PRODUCT_NOT_EXISTED));
//
//        Cart cartItem = Cart.builder()
//                .user(user)
//                .product(product)
//                .quantity(1)
//                .build();
//
//        cartRepository.save(cartItem);
//    }
//}

package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.entity.CartItem;
import com.example.ecommerceweb.entity.product.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.cart c " +
            "JOIN FETCH ci.product p " +
            "LEFT JOIN FETCH ci.attributes ca " +
            "WHERE c.user.id = :userId")
    List<CartItem> findCartItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    List<CartItem> findByCartAndProduct(@Param("cartId") Long cartId, @Param("productId") Long productId);
}

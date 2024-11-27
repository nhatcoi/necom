package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final CartService cartService;

    private final Translator translator;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getItemsCart(@PathVariable Long cartId)
    {
        List<Cart> cartItems = cartService.getCartItems(cartId);
        return ResponseEntity.ok().body(cartItems);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/count")
    public ResponseData<?> getCountInCart() {
        return new ResponseData<>(HttpStatus.OK.value(),  translator.toLocated("response.success"), cartService.getCountInCart());
    }
}

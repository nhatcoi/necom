package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.entity.CartItem;
import com.example.ecommerceweb.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final CartService cartService;

    @GetMapping("/shopping-cart")
    public String shoppingCart() {
        return "shopping-cart";
    }

    @GetMapping("/shopping-cart/{cartId}")
    @ResponseBody
    public ResponseEntity<?> getItemsCart(@PathVariable Long cartId)
    {
        List<CartItem> cartItems = cartService.getCartItems(cartId);
        return ResponseEntity.ok().body(cartItems);
    }

    @GetMapping("/shopping-cart/count/{cartId}")
    @ResponseBody
    public ResponseEntity<?> getCountInCart(@PathVariable Long cartId) {
        return ResponseEntity.ok().body(cartService.getCountInCart(cartId));
    }
}

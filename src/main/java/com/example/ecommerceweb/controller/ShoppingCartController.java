package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.request.UpdateCartItemRequest;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final CartService cartService;
    private final Translator translator;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/items")
    public ResponseData<?> getCart() {
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("get.cart.items.ok"),
                cartService.getCartItems()
        );
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/count")
    public ResponseData<?> getCountInCart() {
        return new ResponseData<>(HttpStatus.OK.value(),  translator.toLocated("response.success"), cartService.getTotalInCart());
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove/{id}")
    public ResponseData<?> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("delete.cart.item.ok"), null);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseData<?> updateCartItem(@PathVariable Long id,
                                          @RequestBody @Valid UpdateCartItemRequest request) {

        cartService.updateCartItem(id, request.getQuantity());

        return new ResponseData<>(
                HttpStatus.OK.value(),
                "quantity updated successfully.",
                null
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-to-cart/{productId}")
    public ResponseData<?> createCartItem(@PathVariable Long productId) {
        Integer totalInCart = cartService.createCartItem(productId);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Cart item updated successfully.",
                totalInCart
        );
    }
}

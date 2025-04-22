package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.cart.CartRequest;
import com.example.ecommerceweb.dto.cart.UpdateCartItemRequest;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.dto.cart.CartResponse;
import com.example.ecommerceweb.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final Translator translator;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/items")
    public ResponseData<?> getItemsInCart() {
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("get.cart.items.ok"),
                cartService.getCartItems()
        );
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseData<?> createCartItem(@RequestBody CartRequest cartRequest) {
        CartResponse response = cartService.createCartItem(cartRequest);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("Add product to cart successfully"),
                response
        );
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove/{id}")
    public ResponseData<?> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("Delete items in cart successfully!"), null);
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
    @DeleteMapping("/clear")
    public ResponseData<?> clearCart() {
        cartService.clearCart();
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                null
        );
    }


}

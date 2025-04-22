package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.order.CheckoutRequest;
import com.example.ecommerceweb.dto.order.StripeResponse;
import com.example.ecommerceweb.service.services_impl.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckOutController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public PaymentIntent createPayment(@RequestParam Long amount, @RequestParam String currency) throws StripeException {
        return paymentService.createPaymentIntent(amount, currency);
    }

    @PostMapping("/checkout-list")
    public ResponseEntity<StripeResponse> checkoutProductList(@RequestBody CheckoutRequest checkoutRequest) {
        StripeResponse stripeResponse = paymentService.checkoutProductList(checkoutRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }




}

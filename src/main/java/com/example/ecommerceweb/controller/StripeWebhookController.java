package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.enums.StatusEnum;
import com.example.ecommerceweb.service.CartService;
import com.example.ecommerceweb.service.OrderService;
import com.example.ecommerceweb.service.services_impl.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CartService cartService;

    @Value("${stripe.webhook}")
    private String webhookSecret;

    @PostMapping("")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_method.attached":
                handlePaymentMethodAttached(event);
                break;
            default:
                log.warn("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Event processed");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        Map<String, String> metadata = paymentIntent.getMetadata();

        Optional.ofNullable(metadata)
                .map(m -> m.get("orderId"))
                .map(Long::valueOf)
                .ifPresent(orderId -> {
                    orderService.updateOrderStatus(orderId, StatusEnum.PAID);

                    Optional.ofNullable(metadata.get("productIds"))
                            .map(productIdsString -> Arrays.stream(productIdsString.split(","))
                                    .map(Long::valueOf)
                                    .collect(Collectors.toList()))
                            .ifPresent(productIds -> {
                                String username = metadata.get("username");
                                cartService.removeItems(username, productIds);
                            });
                });

        if (metadata == null || !metadata.containsKey("orderId")) {
            log.warn("Order ID not found in metadata");
        }
    }

    private void handlePaymentMethodAttached(Event event) {
        log.info("Payment Method Attached: {}", event.getData().getObject().toString());
    }
}

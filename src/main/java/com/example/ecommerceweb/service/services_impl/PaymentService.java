package com.example.ecommerceweb.service.services_impl;


import com.example.ecommerceweb.dto.order.CheckoutRequest;
import com.example.ecommerceweb.dto.product.ProductOrderRequest;
import com.example.ecommerceweb.dto.order.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String SUCCESS_URL = "http://localhost:5173/checkout";
    private static final String CANCEL_URL = "http://localhost:5173/checkout";
    private static final String DEFAULT_CURRENCY = "usd";

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        validateCurrency(currency);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", List.of("cart"));

        return PaymentIntent.create(params);
    }

    public StripeResponse checkoutProductList(CheckoutRequest checkoutRequest) {
        List<SessionCreateParams.LineItem> lineItems = createLineItems(checkoutRequest);

        String productIdsAsString = extractProductIds(checkoutRequest);
        String username = getAuthenticatedUsername();

        Session session = createPaymentSession(checkoutRequest, lineItems, productIdsAsString, username);

        if (session == null) {
            return buildErrorResponse();
        }

        return buildSuccessResponse(session);
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must be specified");
        }
    }

    private List<SessionCreateParams.LineItem> createLineItems(CheckoutRequest checkoutRequest) {
        return checkoutRequest.getProducts().stream()
                .map(this::createLineItem)
                .collect(Collectors.toList());
    }

    private SessionCreateParams.LineItem createLineItem(ProductOrderRequest product) {
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(product.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(Optional.ofNullable(product.getCurrency()).map(String::toLowerCase).orElse(DEFAULT_CURRENCY))
                        .setUnitAmount(product.getAmount())
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) Optional.ofNullable(product.getQuantity()).filter(q -> q > 0).orElse(1))
                .setPriceData(priceData)
                .build();
    }

    private String extractProductIds(CheckoutRequest checkoutRequest) {
        return checkoutRequest.getProducts().stream()
                .map(product -> String.valueOf(product.getId()))
                .collect(Collectors.joining(","));
    }

    private String getAuthenticatedUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }

    private Session createPaymentSession(CheckoutRequest checkoutRequest, List<SessionCreateParams.LineItem> lineItems,
                                         String productIdsAsString, String username) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addAllLineItem(lineItems)
                .setPaymentIntentData(buildPaymentIntentData(checkoutRequest, productIdsAsString, username))
                .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            log.error("Stripe Exception occurred: ", e);
            return null;
        }
    }

    private SessionCreateParams.PaymentIntentData buildPaymentIntentData(CheckoutRequest checkoutRequest,
                                                                         String productIdsAsString, String username) {
        return SessionCreateParams.PaymentIntentData.builder()
                .putMetadata("orderId", String.valueOf(checkoutRequest.getOrderMetadata().getOrderId()))
                .putMetadata("productIds", productIdsAsString)
                .putMetadata("username", username)
                .putMetadata("fullName", checkoutRequest.getOrderMetadata().getFullName())
                .putMetadata("email", checkoutRequest.getOrderMetadata().getEmail())
                .putMetadata("phoneNumber", checkoutRequest.getOrderMetadata().getPhoneNumber())
                .build();
    }

    private StripeResponse buildErrorResponse() {
        return StripeResponse.builder()
                .status("FAILED")
                .message("Failed to create payment session")
                .build();
    }

    private StripeResponse buildSuccessResponse(Session session) {
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created successfully")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

}

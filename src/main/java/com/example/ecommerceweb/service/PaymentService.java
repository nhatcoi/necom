package com.example.ecommerceweb.service;


import com.example.ecommerceweb.dto.request.CheckoutRequest;
import com.example.ecommerceweb.dto.request.ProductRequest;
import com.example.ecommerceweb.dto.response.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", List.of("cart"));

        return PaymentIntent.create(params);
    }

    public StripeResponse checkoutProductList(CheckoutRequest checkoutRequest) {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (ProductRequest product : checkoutRequest.getProducts()) {
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(product.getName())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(product.getCurrency() != null ? product.getCurrency().toLowerCase() : "usd")
                            .setUnitAmount(product.getAmount() * 100L)
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(product.getQuantity() != null && product.getQuantity() > 0 ? product.getQuantity() : 1)
                            .setPriceData(priceData)
                            .build();

            lineItems.add(lineItem);
        }

        // Tạo session với tất cả LineItems
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://1303-2a09-bac5-d45b-16d2-00-246-bd.ngrok-free.app")
                        .setCancelUrl("https://1303-2a09-bac5-d45b-16d2-00-246-bd.ngrok-free.app")
                        .addAllLineItem(lineItems)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.error("Stripe Exception occurred: ", e);
            return com.example.ecommerceweb.dto.response.StripeResponse.builder()
                    .status("FAILED")
                    .message("Failed to create payment session: " + e.getMessage())
                    .build();
        }

        if (session == null) {
            return StripeResponse.builder()
                    .status("FAILED")
                    .message("Failed to create payment session")
                    .build();
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created successfully")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }


    public StripeResponse checkoutProducts(ProductRequest productRequest) {

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://1303-2a09-bac5-d45b-16d2-00-246-bd.ngrok-free.app")
                        .setCancelUrl("https://1303-2a09-bac5-d45b-16d2-00-246-bd.ngrok-free.app")
                        .addLineItem(lineItem)
                        .build();

        // Create new session
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.error(e.getMessage());
        }

        return StripeResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created ")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}

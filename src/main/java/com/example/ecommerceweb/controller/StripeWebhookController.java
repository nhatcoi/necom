package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.StripeConfig;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String stripeSignature,
                                                      HttpServletRequest request) {
        try {
            // Xác thực chữ ký của webhook
            Event event = Webhook.constructEvent(payload, stripeSignature, webhookSecret);

            // Kiểm tra loại sự kiện
            if ("payment_intent.succeeded".equals(event.getType())) {
                // Lấy thông tin PaymentIntent từ sự kiện
                com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().get();

                // Xử lý khi thanh toán thành công
                handlePaymentIntentSucceeded(paymentIntent);
            } else {
                // Xử lý các sự kiện khác nếu cần
                System.out.println("Unhandled event type: " + event.getType());
            }

            // Trả về response thành công
            return ResponseEntity.ok("Event processed successfully");

        } catch (SignatureVerificationException e) {
            // Nếu chữ ký không hợp lệ
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            // Xử lý lỗi chung
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    // Phương thức xử lý thanh toán thành công
    private void handlePaymentIntentSucceeded(com.stripe.model.PaymentIntent paymentIntent) {
        // Lấy các thông tin chi tiết từ PaymentIntent
        String paymentIntentId = paymentIntent.getId();
        String customerId = paymentIntent.getCustomer();
        long amountReceived = paymentIntent.getAmountReceived();
        String currency = paymentIntent.getCurrency();

        // Ví dụ: Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu của bạn
        // Order order = orderService.findByPaymentIntentId(paymentIntentId);
        // order.setStatus("PAID");
        // orderService.save(order);

        // Log thông tin thanh toán
        System.out.println("PaymentIntent " + paymentIntentId + " was successful for amount " + amountReceived + " " + currency);
    }

}

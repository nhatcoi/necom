package com.example.ecommerceweb.dto.response.cart;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    private LocalDateTime orderDate;
    private String status;
    private Double totalPrice;
    private String shippingMethod;
    private String shippingAddress;
    private Date shippingDate;
    private String trackingNumber;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private Boolean active;
}

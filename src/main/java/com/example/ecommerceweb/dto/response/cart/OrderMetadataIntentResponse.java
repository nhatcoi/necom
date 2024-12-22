package com.example.ecommerceweb.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMetadataIntentResponse {
    private Long orderId;
    private String fullName;
    private String email;
    private String phoneNumber;
}

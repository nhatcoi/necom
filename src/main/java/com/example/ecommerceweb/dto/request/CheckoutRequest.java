package com.example.ecommerceweb.dto.request;

import com.example.ecommerceweb.dto.response.OrderMetadataIntentResponse;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private List<ProductRequest> products;
    private OrderMetadataIntentResponse orderMetadata;
}

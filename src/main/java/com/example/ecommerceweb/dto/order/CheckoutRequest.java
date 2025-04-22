package com.example.ecommerceweb.dto.order;

import com.example.ecommerceweb.dto.product.ProductOrderRequest;
import com.example.ecommerceweb.dto.cart.OrderMetadataIntentDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private List<ProductOrderRequest> products;
    private OrderMetadataIntentDTO orderMetadata;
}

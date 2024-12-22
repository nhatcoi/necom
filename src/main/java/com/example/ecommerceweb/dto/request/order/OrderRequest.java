package com.example.ecommerceweb.dto.request.order;

import com.example.ecommerceweb.dto.request.product.ProductOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<ProductOrderRequest> products;
    private OrderInfoRequest orderInfo;
}

package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.order.OrderRequest;
import com.example.ecommerceweb.dto.cart.OrderMetadataIntentDTO;
import com.example.ecommerceweb.dto.order.OrderResponse;
import com.example.ecommerceweb.enums.StatusEnum;
import com.example.ecommerceweb.filter.OrderFilter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    OrderMetadataIntentDTO createOrder(OrderRequest orderRequest);

    void updateOrderStatus(Long orderId, StatusEnum statusEnum);

    String getPaymentStatus(Long orderId);

    Page<OrderResponse> getAllOrders(int page, int size, String status, String search);

    Page<OrderResponse> getOrders(OrderFilter orderFilter, Pageable pageable);

    OrderResponse getOrderById(Long orderId);
}

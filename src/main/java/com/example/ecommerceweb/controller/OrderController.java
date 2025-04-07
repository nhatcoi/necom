package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.request.order.OrderRequest;
import com.example.ecommerceweb.dto.response.Pagination;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.dto.response.order.OrderResponse;
import com.example.ecommerceweb.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Translator translator;

    @PostMapping("/create-order")
    public ResponseData<?> createOrderAndOrderDetails(@RequestBody OrderRequest orderRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), orderService.createOrder(orderRequest));
    }

    @GetMapping("/payment-status/{orderId}")
    public ResponseData<?> getPaymentStatus(@PathVariable Long orderId) {
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), orderService.getPaymentStatus(orderId));
    }

    @GetMapping
    public ResponseData<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page-1, size, status);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                orders.getContent(),
                new Pagination(orders)
        );
    }

}

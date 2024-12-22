package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.request.order.OrderRequest;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Translator translator;

    @PostMapping("")
    public ResponseData<?> createOrderAndOrderDetails(@RequestBody OrderRequest orderRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), orderService.createOrder(orderRequest));
    }
}

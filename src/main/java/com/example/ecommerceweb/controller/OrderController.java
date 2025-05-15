package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.order.OrderRequest;
import com.example.ecommerceweb.dto.response_data.Pagination;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.filter.OrderFilter;
import com.example.ecommerceweb.dto.order.OrderResponse;
import com.example.ecommerceweb.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("")
    public ResponseData<?> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "100") int size,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "search", defaultValue = "") String search,
            @ModelAttribute OrderFilter orderFilter
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page, size, status, search, orderFilter);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                orders.getContent(),
                new Pagination(orders)
        );
    }

    @GetMapping("/managed")
    public ResponseData<?> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "100") int size,
            @ModelAttribute OrderFilter orderFilter
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getOrders(orderFilter, pageRequest);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                orders.getContent(),
                new Pagination(orders)
        );
    }
    

    @GetMapping("/{orderId}")
    public ResponseData<?> getOrderById(@PathVariable Long orderId) {
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), orderService.getOrderById(orderId));
    }

}

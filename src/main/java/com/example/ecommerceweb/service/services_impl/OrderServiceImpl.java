package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.OrderInfoRequest;
import com.example.ecommerceweb.dto.request.OrderRequest;
import com.example.ecommerceweb.dto.request.ProductOrderRequest;
import com.example.ecommerceweb.dto.response.OrderMetadataIntentResponse;
import com.example.ecommerceweb.entity.Order;
import com.example.ecommerceweb.entity.OrderDetail;
import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.StatusEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.OrderDetailRepository;
import com.example.ecommerceweb.repository.OrderRepository;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OrderMetadataIntentResponse createOrder(OrderRequest orderRequest) {
        User user = getAuthenticatedUser();
        Order order = createOrderFromRequest(orderRequest, user);
        orderRepository.save(order);

        List<OrderDetail> orderDetails = createOrderDetails(orderRequest, order);
        orderDetailRepository.saveAll(orderDetails);

        return buildOrderMetadataResponse(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, StatusEnum statusEnum) {
        String status = statusEnum.name();
        orderRepository.updateOrderStatusById(orderId, status);
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
    }

    private Order createOrderFromRequest(OrderRequest orderRequest, User user) {
        OrderInfoRequest orderInfoRequest = orderRequest.getOrderInfo();

        return Order.builder()
                .user(user)
                .fullName(orderInfoRequest.getFullName())
                .email(orderInfoRequest.getEmail())
                .phoneNumber(orderInfoRequest.getPhoneNumber())
                .address(formatAddress(orderInfoRequest))
                .note("note")
                .orderDate(LocalDateTime.now())
                .status(String.valueOf(StatusEnum.INITIATED))
                .totalPrice(orderInfoRequest.getTotalPrice())
                .shippingMethod("SHIPPING")
                .shippingAddress("SHIPPING ADDRESS")
                .shippingDate(LocalDateTime.now().plusDays(1))
                .trackingNumber("trackingNumber")
                .paymentMethod("PAYMENT")
                .paymentDate(LocalDateTime.now())
                .active(true)
                .build();
    }

    private String formatAddress(OrderInfoRequest orderInfoRequest) {
        return String.format("%s, %s, %s, %s",
                orderInfoRequest.getPostcode(),
                orderInfoRequest.getAddressLine(),
                orderInfoRequest.getCity(),
                orderInfoRequest.getCountry());
    }

    private List<OrderDetail> createOrderDetails(OrderRequest orderRequest, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ProductOrderRequest pod : orderRequest.getProducts()) {
            Product product = entityManager.getReference(Product.class, pod.getId());
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(Float.valueOf(pod.getAmount()))
                    .numberOfProducts(pod.getQuantity())
                    .totalPrice((float) (pod.getAmount() * pod.getQuantity()))
                    .build();
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    private OrderMetadataIntentResponse buildOrderMetadataResponse(Order order) {
        return OrderMetadataIntentResponse.builder()
                .orderId(order.getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .build();
    }

}

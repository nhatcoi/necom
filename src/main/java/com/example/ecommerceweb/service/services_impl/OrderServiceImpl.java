package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.product.ProductDTO;
import com.example.ecommerceweb.dto.order.OrderInfoRequest;
import com.example.ecommerceweb.dto.order.OrderRequest;
import com.example.ecommerceweb.dto.product.ProductOrderRequest;
import com.example.ecommerceweb.dto.cart.OrderMetadataIntentDTO;
import com.example.ecommerceweb.dto.order.OrderResponse;
import com.example.ecommerceweb.entity.Order;
import com.example.ecommerceweb.entity.OrderDetail;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.entity.product.ProductAttribute;
import com.example.ecommerceweb.enums.StatusEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.OrderDetailRepository;
import com.example.ecommerceweb.repository.OrderRepository;
import com.example.ecommerceweb.security.SecurityUtils;
import com.example.ecommerceweb.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SecurityUtils securityUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OrderMetadataIntentDTO createOrder(OrderRequest orderRequest) {
        User user = securityUtils.getCurrentUser();
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


    @Override
    public String getPaymentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found"));
        return order.getStatus();
    }

    @Override
    public Page<OrderResponse> getAllOrders(int page, int size, String status) {
        User user = securityUtils.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        orders = orderRepository.findAllByStatus(user.getId(), status.toUpperCase(Locale.ROOT), pageable);

        return orders.map(order -> OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .items(order.getOrderDetails().stream().map(item -> {
                    Product product = item.getProduct();
                    return ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .thumbnail(product.getThumbnail())
                            .description(product.getDescription())
                            .attributes(item.getAttributes())
                            .quantity(item.getNumberOfProducts())
                            .originalPrice(BigDecimal.valueOf(product.getPrice()))
                            .sellingPrice(BigDecimal.valueOf(item.getPrice()))
                            .build();
                }).toList())
                .total(BigDecimal.valueOf(order.getTotalPrice()))
                .build());
    }

    @Override
    public String getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found"));

        return null;

    }

    private Order createOrderFromRequest(OrderRequest orderRequest, User user) {
        OrderInfoRequest orderInfoRequest = orderRequest.getOrderInfo();

        return Order.builder()
                .user(user)
                .fullName(orderInfoRequest.getFullName())
                .email(orderInfoRequest.getEmail())
                .phoneNumber(orderInfoRequest.getPhoneNumber())
                .address(formatAddress(orderInfoRequest))
                .note(orderInfoRequest.getNote())
                .orderDate(LocalDateTime.now())
                .status(String.valueOf(StatusEnum.PENDING))
                .totalPrice(orderInfoRequest.getTotalPrice())
                .shippingMethod("SHIPPING")
                .shippingAddress("SHIPPING ADDRESS")
                .shippingDate(LocalDateTime.now().plusDays(1))
                .trackingNumber("trackingNumber")
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentDate(LocalDateTime.now())
                .active(true)
                .build();
    }

    private String formatAddress(OrderInfoRequest orderInfoRequest) {
        return String.format("%s, %s, %s, %s, %s",
                orderInfoRequest.getPostcode(),
                orderInfoRequest.getAddressLine(),
                orderInfoRequest.getDistrict(),
                orderInfoRequest.getCity(),
                orderInfoRequest.getCountry());
    }

    private List<OrderDetail> createOrderDetails(OrderRequest orderRequest, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ProductOrderRequest pod : orderRequest.getProducts()) {
            Product product = entityManager.getReference(Product.class, pod.getId());

            // example: orderRequest.getProducts().getAttributes() == "Color: Red; Capacity: 128GB"
            String[] attributePairs = pod.getAttributes().split(",");

            // Create a map to store the attributes
            Map<String, String> attributeMap = new HashMap<>();

            for (String pair : attributePairs) {
                // Split each pair by colon to separate the key and value
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    // Trim whitespace and add to the map
                    attributeMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }



            // check if the product has the same attributes
            Float priceWithAttribute = product.getPrice();

            for (ProductAttribute pa : product.getAttributes()) {
                String attributeName = pa.getAttributeValue().getAttribute().getName();
                String attributeValue = pa.getAttributeValue().getValue();

                if (attributeMap.containsKey(attributeName) &&
                    attributeMap.get(attributeName).equals(attributeValue)) {
                    priceWithAttribute += pa.getPrice();
                }
            }

            if (!Objects.equals(priceWithAttribute, pod.getPrice())) {
                throw new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Price not match");
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(priceWithAttribute)
                    .numberOfProducts(pod.getQuantity())
                    .totalPrice(priceWithAttribute * pod.getQuantity())
                    .attributes(pod.getAttributes())
                    .build();
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

    private OrderMetadataIntentDTO buildOrderMetadataResponse(Order order) {
        return OrderMetadataIntentDTO.builder()
                .orderId(order.getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .build();
    }

}

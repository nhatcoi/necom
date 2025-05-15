package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.product.ProductDTO;
import com.example.ecommerceweb.dto.order.OrderInfoRequest;
import com.example.ecommerceweb.dto.order.OrderRequest;
import com.example.ecommerceweb.dto.product.ProductOrderRequest;
import com.example.ecommerceweb.dto.cart.OrderMetadataIntentDTO;
import com.example.ecommerceweb.dto.order.OrderResponse;
import com.example.ecommerceweb.entity.Order;
import com.example.ecommerceweb.entity.OrderDetail;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.entity.product.ProductAttribute;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.enums.StatusEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.filter.OrderFilter;
import com.example.ecommerceweb.repository.OrderDetailRepository;
import com.example.ecommerceweb.repository.OrderRepository;
import com.example.ecommerceweb.repository.ProductRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SecurityUtils securityUtils;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OrderMetadataIntentDTO createOrder(OrderRequest orderRequest) {
        User user = securityUtils.getCurrentUser();
        // Tạo order
        Order order = createOrderFromRequest(orderRequest, user);
        orderRepository.save(order);

        // Validate job và tạo order details cho order
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
    public Page<OrderResponse> getAllOrders(int page, int size, String status, String search, OrderFilter orderFilter) {
        User user = securityUtils.getCurrentUser();

        for (Role role : user.getRoles()) {
            if (role.getName().equals(RoleEnum.ADMIN.name())) {
                return getOrders(orderFilter, PageRequest.of(page, size));
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        orders = orderRepository.findAllByStatus(
                user.getId(),
                status.toUpperCase(Locale.ROOT),
                search,
                pageable
        );

        return orders.map(order -> OrderResponse.builder()
                .id(order.getId())
                .customerName(user.getFullName())
                .email(user.getEmail())
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
                            .originalPrice(product.getPrice())
                            .sellingPrice(item.getPrice())
                            .build();
                }).toList())
                .total(order.getTotalPrice())
                .build());
    }

    @Override
    public Page<OrderResponse> getOrders(OrderFilter orderFilter, Pageable pageable) {


        Specification<Order> spec = Specification.where(null);

        if (orderFilter.getSearch() != null && !orderFilter.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + orderFilter.getSearch().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("id").as(String.class)), searchPattern),
                    cb.like(cb.lower(root.get("fullName")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern)
                )
            );
        }

        if (orderFilter.getStatus() != null && !orderFilter.getStatus().trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("status"), orderFilter.getStatus().toUpperCase(Locale.ROOT))
            );
        }

        if (orderFilter.getPaymentStatus() != null && !orderFilter.getPaymentStatus().trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("paymentStatus"), orderFilter.getPaymentStatus().toUpperCase(Locale.ROOT))
            );
        }

        if (orderFilter.getDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("orderDate"), orderFilter.getDateFrom().atStartOfDay())
            );
        }

        if (orderFilter.getDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("orderDate"), orderFilter.getDateTo().plusDays(1).atStartOfDay())
            );
        }

        if (orderFilter.getSortBy() == null) {
            // default desc
            spec = spec.and((root, query, cb) -> {
                assert query != null;
                if (query.getResultType() != Long.class) {
                    query.orderBy(cb.desc(root.get("orderDate")));
                }
                return null;
            });
        }

        if (orderFilter.getSortBy() != null && !orderFilter.getSortBy().trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                assert query != null;
                if (query.getResultType() != Long.class) {
                    String[] sortParts = orderFilter.getSortBy().split("_");
                    if (sortParts.length == 2) {
                        String field = sortParts[0];
                        String direction = sortParts[1];
                        
                        if ("desc".equalsIgnoreCase(direction)) {
                            query.orderBy(cb.desc(root.get(field)));
                        } else {
                            query.orderBy(cb.asc(root.get(field)));
                        }
                    }
                }
                return null;
            });
        }

        Page<Order> orders = orderRepository.findAll(spec, pageable);
        
        return orders.map(order -> OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .customerName(order.getFullName())
                .email(order.getEmail())
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
                            .originalPrice(product.getPrice())
                            .sellingPrice(item.getPrice())
                            .build();
                }).toList())
                .total(order.getTotalPrice())
                .build());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found"));
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .email(order.getEmail())
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
                            .originalPrice(product.getPrice())
                            .sellingPrice(item.getPrice())
                            .build();
                }).toList())
                .total(order.getTotalPrice())
                .active(true)
                .build();
            
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
            Product product = productRepository.findById(pod.getId()).orElseThrow(() ->
                    new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Product not found"));

            // Định dạng variants từ request, ex: "Color: Red, Capacity: 128GB"
            String[] attributePairs = pod.getAttributes().split(";");

            // Map để trữ variants
            Map<String, String> attributeMap = new HashMap<>();

            for (String pair : attributePairs) {
                // Split each pair by colon to separate the key and value
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    // Trim whitespace and add to the map
                    attributeMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }

            // Tính giá cuối cùng từ các attributes được chọn
            BigDecimal totalAttributePrice = BigDecimal.ZERO;
            int matchedAttributeCount = 0;

            for (ProductAttribute pa : product.getAttributes()) {
                String attributeName = pa.getAttributeValue().getAttribute().getName();
                String attributeValue = pa.getAttributeValue().getValue();

                if (attributeMap.containsKey(attributeName) &&
                        attributeMap.get(attributeName).equals(attributeValue)) {

                    totalAttributePrice = totalAttributePrice.add(pa.getPrice());
                    matchedAttributeCount++;
                }
            }

            BigDecimal expectedPrice = totalAttributePrice;
            if (matchedAttributeCount > 1) {
                BigDecimal deduction = product.getPrice().multiply(BigDecimal.valueOf(matchedAttributeCount - 1));
                expectedPrice = expectedPrice.subtract(deduction);
            }

            // So sánh với giá với client
            if (expectedPrice.compareTo(pod.getPrice()) != 0) {
                throw new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Price not match");
            }

            // Tạo OrderDetail job
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(totalAttributePrice)
                    .numberOfProducts(pod.getQuantity())
                    .totalPrice(totalAttributePrice.multiply(BigDecimal.valueOf(pod.getQuantity())))
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

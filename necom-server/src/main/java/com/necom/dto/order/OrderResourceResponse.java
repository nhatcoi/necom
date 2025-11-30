package com.necom.dto.order;

import com.necom.dto.customer.CustomerResourceResponse;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class OrderResourceResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String code;
    private String name;
    private String color;
    @Nullable
    private CustomerResourceResponse customerResource;
    private Integer status;
}

package com.necom.dto.customer;

import com.necom.dto.authentication.UserResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class CustomerResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private UserResponse user;
    private CustomerGroupResponse customerGroup;
    private CustomerStatusResponse customerStatus;
    private CustomerResourceResponse customerResource;
}

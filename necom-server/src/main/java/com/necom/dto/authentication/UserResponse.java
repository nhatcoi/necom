package com.necom.dto.authentication;

import com.necom.dto.address.AddressResponse;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String gender;
    private AddressResponse address;
    private String avatar;
    private Integer status;
    private Set<RoleResponse> roles;
}

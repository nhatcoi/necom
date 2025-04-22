package com.example.ecommerceweb.dto.user;

import com.example.ecommerceweb.enums.RoleEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private List<AddressRequest> addresses;
    private Set<RoleEnum> roles;
}

package com.example.ecommerceweb.dto.response.user;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirth;
    private List<AddressResponse> addresses;
    private Set<String> roleNames;
}

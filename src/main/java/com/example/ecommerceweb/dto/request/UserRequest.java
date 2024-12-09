package com.example.ecommerceweb.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
}

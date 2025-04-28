package com.example.ecommerceweb.dto.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String avatar;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddressResponse> addresses;
    private Set<String> roleNames;
}

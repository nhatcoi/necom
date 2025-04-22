package com.example.ecommerceweb.dto.user;

import lombok.*;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String password;
    private String phoneNumber;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private Boolean isActive;

}

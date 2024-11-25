package com.example.ecommerceweb.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String password;
    private String phoneNumber;
    private String fullName;
    private String address;
    private LocalDate dateOfBirth;
}

package com.example.ecommerceweb.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
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
    private String address;
    private LocalDate dateOfBirth;
    private Set<String> roleNames;
}

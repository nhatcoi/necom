package com.example.ecommerceweb.dto.request.auth;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    private String userIdentifier;
    private String password;
}

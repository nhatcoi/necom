package com.example.ecommerceweb.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isAuthenticated;
}

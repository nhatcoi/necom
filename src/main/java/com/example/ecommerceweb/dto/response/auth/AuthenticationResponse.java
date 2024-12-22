package com.example.ecommerceweb.dto.response.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean isAuthenticated;
}

package com.example.ecommerceweb.dtos.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private String token;
    private boolean isAuthenticated;
}

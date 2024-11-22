package com.example.ecommerceweb.dto.response;

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

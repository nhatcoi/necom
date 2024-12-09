package com.example.ecommerceweb.dto.request;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    private String userIdentifier;
    private String password;
}

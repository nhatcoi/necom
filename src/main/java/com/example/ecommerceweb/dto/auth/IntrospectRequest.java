package com.example.ecommerceweb.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntrospectRequest {
    private String token;
}

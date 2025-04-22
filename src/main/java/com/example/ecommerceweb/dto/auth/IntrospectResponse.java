package com.example.ecommerceweb.dto.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntrospectResponse {
    private boolean valid;
}
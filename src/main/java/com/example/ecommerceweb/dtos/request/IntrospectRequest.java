package com.example.ecommerceweb.dtos.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IntrospectRequest {
    private String token;
}

package com.example.ecommerceweb.dto.request;

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

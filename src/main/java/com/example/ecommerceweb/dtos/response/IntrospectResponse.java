package com.example.ecommerceweb.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IntrospectResponse {
    private boolean valid;
}
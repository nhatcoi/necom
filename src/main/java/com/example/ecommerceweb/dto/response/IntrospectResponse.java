package com.example.ecommerceweb.dto.response;

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
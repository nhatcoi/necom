package com.necom.dto.authentication;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}

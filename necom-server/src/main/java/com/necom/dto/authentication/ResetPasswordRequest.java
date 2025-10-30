package com.necom.dto.authentication;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String email;
    private String password;
}

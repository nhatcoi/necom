package com.example.ecommerceweb.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // System errors
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Validation errors
    INVALID_KEY(1001, "Invalid key provided", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    // Authentication and Authorization errors
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),

    // User-related errors
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1010, "Phone number already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User does not exist", HttpStatus.NOT_FOUND),
    PHONE_NUMBER_NOT_EXISTED(1009, "Phone number is invalid", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED_USER(1011, "Unauthenticated user", HttpStatus.UNAUTHORIZED),
    CART_NOT_FOUND(1012, "Cart item not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

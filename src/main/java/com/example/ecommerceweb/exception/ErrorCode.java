package com.example.ecommerceweb.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {


    // Authentication and Authorization errors
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "You do not have permission"),

    // User-related errors
    USER_EXISTED(HttpStatus.BAD_REQUEST, "User already exists"),
    USER_NOT_EXISTED(HttpStatus.NOT_FOUND, "User does not exist"),
    EMAIL_NOT_EXISTED(HttpStatus.NOT_FOUND, "Email does not exist"),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),

    // Token
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token not found"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid token"),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "Token has been revoked"),

    // SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Password does not match"),

    // Authentication errors
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    
    // Resource errors
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Resource already exists"),
    RESOURCE_INVALID(HttpStatus.BAD_REQUEST, "Invalid input data"),
    
    // Validation errors
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Invalid data format"),
    
    // Business logic errors
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "Invalid order status"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment failed"),

    // Forbidden Errors (403)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "Insufficient permissions"),
    OPERATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Operation not allowed"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Account is locked"),

    // Conflict Errors (409)
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Phone number already exists"),

    // Unsupported Media Type (415)
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),

    // Data common existed
    DATA_EXISTED(HttpStatus.CONFLICT, "Data existed"),

    // Not Found Errors (404)
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found");




    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    ErrorCode(HttpStatus status, String message) {
        this.code = 0; // Placeholder for the new constructor
        this.message = message;
        this.status = status;
    }
}

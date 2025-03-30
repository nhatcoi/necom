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
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),

    // User-related errors
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1010, "Phone number already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(HttpStatus.NOT_FOUND, "User does not exist"),
    PHONE_NUMBER_NOT_EXISTED(HttpStatus.NOT_FOUND, "Phone number does not exist"),
    UNAUTHENTICATED_USER(1011, "Unauthenticated user", HttpStatus.UNAUTHORIZED),
    CART_NOT_FOUND(1012, "Cart item not found", HttpStatus.NOT_FOUND),
    CART_QUANTITY_LIMIT(1013, "Limit the quantity of a product in the shopping cart", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(HttpStatus.NOT_FOUND, "Product not found"),
    CART_QUANTITY_NOT_VALID(1015, "Cart in quantity not valid", HttpStatus.BAD_REQUEST),

    // common
    RESOURCE_NOT_FOUND(1016, "Resource not found", HttpStatus.NOT_FOUND),

    // Token
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token not found"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid token"),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "Token has been revoked"),

    // SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

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

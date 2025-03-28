package com.example.ecommerceweb.exception;

import lombok.Getter;

@Getter
public class ResourceException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ResourceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}


package com.example.ecommerceweb.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ResourceException extends RuntimeException {
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    public ResourceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
    }

    public ResourceException(ErrorCode errorCode, String customMessage) {
        this.code = errorCode.getCode();
        this.message = customMessage != null ? customMessage : errorCode.getMessage();
        this.httpStatus = errorCode.getStatus();
    }
}


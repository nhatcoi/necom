package com.example.ecommerceweb.exception;

import com.example.ecommerceweb.dto.response_data.ResponseError;
import com.google.api.gax.rpc.UnauthenticatedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ResponseError> handleUnauthorized(ResourceException ex, WebRequest request) {
        // Kiểm tra mã lỗi từ ErrorCode
        HttpStatus status = ex.getErrorCode().getStatus();

        // Tạo ResponseError chứa thông tin lỗi
        ResponseError responseError = new ResponseError(request, status.value());
        responseError.setMessage(ex.getMessage());

        // Trả về ResponseEntity với status đúng
        return new ResponseEntity<>(responseError, status);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseError handleException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ResponseError responseError = new ResponseError(request, HttpStatus.METHOD_NOT_ALLOWED.value());
        responseError.setMessage(ex.getMessage());
        return responseError;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleException(ConstraintViolationException ex, WebRequest request) {
        ResponseError responseError = new ResponseError(request, HttpStatus.BAD_REQUEST.value());
        responseError.setMessage(ex.getMessage());
        return responseError;
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleException(IllegalArgumentException ex, WebRequest request) {
        ResponseError responseError = new ResponseError(request, HttpStatus.BAD_REQUEST.value());
        responseError.setMessage(ex.getMessage());
        return responseError;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ResponseError responseError = new ResponseError(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseError.setMessage("An unexpected error occurred");
        return responseError;
    }
}

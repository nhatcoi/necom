package com.example.ecommerceweb.exception;

import com.example.ecommerceweb.dto.response.ResponseError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleException(MethodArgumentNotValidException ex, WebRequest request) {
        ResponseError responseError = new ResponseError(request, HttpStatus.BAD_REQUEST.value());
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );
        responseError.setErrorMap(errorMap);
        return responseError;
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
        ResponseError responseError = new ResponseError(request, HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseError.setMessage(ex.getMessage());
        return responseError;
    }

}

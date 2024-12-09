package com.example.ecommerceweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseData<T> {
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;


    public ResponseData(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseData(int statusCode, String message, T data, Pagination pagination) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    public ResponseData(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
    }
}

package com.necom.dto.customer;

import lombok.Data;

@Data
public class CustomerResourceRequest {
    private String code;
    private String name;
    private String description;
    private String color;
    private Integer status;
}

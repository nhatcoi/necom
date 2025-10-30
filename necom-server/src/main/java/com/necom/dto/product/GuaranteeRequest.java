package com.necom.dto.product;

import lombok.Data;

@Data
public class GuaranteeRequest {
    private String name;
    private String description;
    private Integer status;
}

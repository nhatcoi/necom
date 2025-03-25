package com.example.ecommerceweb.dto.response.product;

import lombok.Data;

@Data
public class ProductAttributeDTO {
    private String attributeName;
    private String attributeValue;
    private Float price;
    private Integer stockQuantity;
}


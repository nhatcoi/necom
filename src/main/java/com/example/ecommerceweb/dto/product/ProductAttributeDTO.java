package com.example.ecommerceweb.dto.product;

import lombok.Data;

@Data
public class ProductAttributeDTO {
    private Long id;
    private String attributeName;
    private String attributeValue;
    private Float price;
    private Integer stockQuantity;
}


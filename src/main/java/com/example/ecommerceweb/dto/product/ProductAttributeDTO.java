package com.example.ecommerceweb.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductAttributeDTO {
    private Long id;
    private String attributeName;
    private String attributeValue;
    private BigDecimal price;
    private Integer stockQuantity;
}


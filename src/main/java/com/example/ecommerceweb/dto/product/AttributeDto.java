package com.example.ecommerceweb.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AttributeDto {
    private Long id;
    private String name;
    private String value;
    private BigDecimal price;
    private Integer stockQuantity;
}
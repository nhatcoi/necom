package com.example.ecommerceweb.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductFilterRequest {
    private Boolean sortByNew;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Integer category;
    private String search;
    private String sortBy;
    private String status;
}
package com.example.ecommerceweb.filter;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilter {
    private Boolean sortByNew;
    private Boolean inStock;
    private Integer category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}

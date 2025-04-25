package com.example.ecommerceweb.filter;

import org.springframework.data.domain.Sort;

public enum ProductSortOption {
    DEFAULT(null),
    NAME_ASC(Sort.by("name").ascending()),
    NAME_DESC(Sort.by("name").descending()),
    PRICE_ASC(Sort.by("price").ascending()),
    PRICE_DESC(Sort.by("price").descending()),
    STOCK_ASC(Sort.by("quantityInStock").ascending()),
    STOCK_DESC(Sort.by("quantityInStock").descending()),
    NEWEST(Sort.by("createdAt").descending()),
    OLDEST(Sort.by("createdAt").ascending());

    private final Sort sort;

    ProductSortOption(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }

    public static Sort fromString(String input) {
        try {
            return ProductSortOption.valueOf(input.toUpperCase()).getSort();
        } catch (IllegalArgumentException e) {
            return Sort.unsorted();
        }
    }
}
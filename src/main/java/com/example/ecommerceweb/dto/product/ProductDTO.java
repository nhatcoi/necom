package com.example.ecommerceweb.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String thumbnail;
    private String description;
    @JsonProperty("quantity_in_stock")
    private Integer quantityInStock;
    @JsonProperty("category_id")
    private Long categoryId;
    private String categoryName;
    private String attributes;
    private Integer quantity;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private LocalDateTime createdAt;
}

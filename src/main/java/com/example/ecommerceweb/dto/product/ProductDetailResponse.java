package com.example.ecommerceweb.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {

    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    @JsonProperty("quantity_in_stock")
    private Integer quantityInStock;
    @JsonProperty("category_id")
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Float avgRating;
    private List<String> productImages;
    private List<ProductSpecificationDTO> specifications;
    private List<ProductAttributeDTO> attributes;
}

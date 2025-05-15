package com.example.ecommerceweb.dto.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Long categoryId;
    private List<SpecificationDto> specifications;
    private List<AttributeDto> attributes;
    // store file image
    private MultipartFile thumbnail;
    private List<MultipartFile> productImages;
}

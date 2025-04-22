package com.example.ecommerceweb.dto.blog;

import lombok.*;

@Data
@Builder
public class BlogCategoryDTO {
    private Long id;
    private String name;
    private Integer count;
}

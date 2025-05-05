package com.example.ecommerceweb.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer parentId;
}


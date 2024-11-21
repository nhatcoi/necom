package com.example.ecommerceweb.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String imageUrl;
}


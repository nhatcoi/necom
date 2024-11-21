package com.example.ecommerceweb.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogDTO {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
}

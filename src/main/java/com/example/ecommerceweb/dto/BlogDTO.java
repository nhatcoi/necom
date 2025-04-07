package com.example.ecommerceweb.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDTO {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private String thumbnail;
    private String category;
    private LocalDateTime date;
    private Integer views;
    private Integer comments;
    private AuthorDTO author;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorDTO {
        private String name;
        private String avatar;
    }
}
//
//public class BlogDTO {
//    private Long id;
//    private String title;
//    private String content;
//    private String thumbnail;
//    private Long userId;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private Long categoryId;
//}

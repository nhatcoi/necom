package com.example.ecommerceweb.filter;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BlogFilter {
    private String category;
    private String views;
    private String keyword;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean isPublished;
    private String authorName;
}
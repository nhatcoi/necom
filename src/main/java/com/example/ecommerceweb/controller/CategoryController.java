package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<?>> getCategories() {
        List<?> categories = categoryService.getAllCategories();
        return ResponseEntity.ok().body(categories);
    }
}

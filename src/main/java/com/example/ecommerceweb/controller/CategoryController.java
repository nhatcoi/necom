package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final Translator translator;

    @GetMapping("")
    public ResponseData<?> getCategories() {
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("categories.get.success"),
                categoryService.getAllCategories());
    }
}

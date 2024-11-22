package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.CategoryDTO;
import com.example.ecommerceweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    public String createCategory(@ModelAttribute CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
        return "redirect:/";
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<?>> getCategories() {
        List<?> categories = categoryService.getAllCategories();
        return ResponseEntity.ok().body(categories);
    }


    @PutMapping("/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryDTO categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return "redirect:/";
    }
}

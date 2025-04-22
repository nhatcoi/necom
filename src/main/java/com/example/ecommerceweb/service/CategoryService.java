package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.category.CategoryDTO;
import com.example.ecommerceweb.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    Category createCategory(CategoryDTO category);
    Category updateCategory(Long id, CategoryDTO category);
    void deleteCategoryById(Long id);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
}

package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.category.CategoryDTO;
import com.example.ecommerceweb.entity.Category;
import com.example.ecommerceweb.repository.CategoryRepository;
import com.example.ecommerceweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setName(categoryDTO.getName());
            return categoryRepository.save(category);
        }
        return null;
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

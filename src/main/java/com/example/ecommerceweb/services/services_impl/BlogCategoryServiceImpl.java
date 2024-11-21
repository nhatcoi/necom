package com.example.ecommerceweb.services.services_impl;

import com.example.ecommerceweb.entities.BlogCategory;
import com.example.ecommerceweb.repository.BlogCategoryRepository;
import com.example.ecommerceweb.services.BlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepository blogCategoryRepository;

    @Override
    public List<BlogCategory> findAll() {
        return blogCategoryRepository.findAll();
    }
}

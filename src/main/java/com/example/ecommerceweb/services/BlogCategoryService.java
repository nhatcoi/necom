package com.example.ecommerceweb.services;

import com.example.ecommerceweb.entities.BlogCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BlogCategoryService {
    List<BlogCategory> findAll();
}

package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.BlogCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BlogCategoryService {
    List<BlogCategory> findAll();
}

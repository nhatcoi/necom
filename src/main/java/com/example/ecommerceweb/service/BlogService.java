package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.blog.BlogDTO;
import com.example.ecommerceweb.filter.BlogFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BlogService {
    Page<BlogDTO> getAllBlogs(Pageable pageable, BlogFilter blogFilter);
    List<BlogDTO> getRecentBlogs(int limit);
    Page<BlogDTO> getBlogByCategory(Pageable pageable, Long categoryId);
    BlogDTO getBlogById(Long id);
}

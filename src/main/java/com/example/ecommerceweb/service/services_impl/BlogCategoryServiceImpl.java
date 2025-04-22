package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.blog.BlogCategoryDTO;
import com.example.ecommerceweb.entity.BlogCategory;
import com.example.ecommerceweb.repository.BlogCategoryRepository;
import com.example.ecommerceweb.service.BlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final BlogCategoryRepository blogCategoryRepository;

    @Override
    public List<BlogCategory> getAllBlogCategories() {
        return blogCategoryRepository.findAll();
    }

    @Override
    public List<BlogCategoryDTO> getTopBlogCategories() {
        List<Object[]> results = blogCategoryRepository.findTopBlogCategories();

        return results.stream()
                .map(result -> {
                    Long id = (Long) result[0];
                    String name = (String) result[1];
                    Long count = (Long) result[2];

                    return BlogCategoryDTO.builder()
                            .id(id)
                            .name(name)
                            .count(count.intValue())
                            .build();
                })
                .collect(Collectors.toList());

    }

}

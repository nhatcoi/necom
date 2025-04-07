package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.BlogDTO;
import com.example.ecommerceweb.entity.Blog;
import com.example.ecommerceweb.filter.BlogFilter;
import com.example.ecommerceweb.repository.BlogRepository;
import com.example.ecommerceweb.service.BlogService;
import com.example.ecommerceweb.specification.BlogSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;

    @Override
    public BlogDTO getBlogById(Long id) {
        return blogRepository.findById(id)
                .map(blog -> modelMapper.map(blog, BlogDTO.class))
                .orElse(null);
    }

    @Override
    public Page<BlogDTO> getAllBlogs(Pageable pageable, BlogFilter blogFilter) {
        Specification<Blog> spec = Specification.where(null);

        if (blogFilter.getCategory() != null) {
            spec = spec.and(BlogSpecification.hasCategory(blogFilter.getCategory()));
        }

        if (blogFilter.getKeyword() != null && !blogFilter.getKeyword().isBlank()) {
            spec = spec.and(BlogSpecification.hasKeyword(blogFilter.getKeyword()));
        }

        if (blogFilter.getViews() != null) {
            spec = spec.and(BlogSpecification.sortByViews(blogFilter.getViews()));
        } else {
            spec = spec.and(BlogSpecification.sortByCreatedAtDesc());
        }


        Page<Blog> blogs = blogRepository.findAll(spec, pageable);
        return blogs.map(b -> BlogDTO.builder()
                .id(b.getId())
                .title(b.getTitle())
                .excerpt(b.getExcerpt())
                .content(b.getContent())
                .date(b.getCreatedAt())
                .thumbnail(b.getThumbnail())
                .views(b.getViews())
                .author(BlogDTO.AuthorDTO.builder()
                        .name(b.getUser().getFullName())
                        .avatar(b.getUser().getAvatar())
                        .build())
                .category(b.getBlogCategory().getName())

                .build());
    }

    @Override
    public List<BlogDTO> getRecentBlogs(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return blogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(blog -> modelMapper.map(blog, BlogDTO.class))
                .toList();
    }

    @Override
    public Page<BlogDTO> getBlogByCategory(Pageable pageable, Long categoryId) {
        return blogRepository.findAllByCategoryId(pageable, categoryId)
                .map(blog -> modelMapper.map(blog, BlogDTO.class));
    }

}

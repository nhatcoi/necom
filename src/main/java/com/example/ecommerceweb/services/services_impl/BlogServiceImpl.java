package com.example.ecommerceweb.services.services_impl;

import com.example.ecommerceweb.dtos.BlogDTO;
import com.example.ecommerceweb.dtos.PaginatedResponse;
import com.example.ecommerceweb.repository.BlogRepository;
import com.example.ecommerceweb.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
    public Page<BlogDTO> getAllBlogs(Pageable pageable) {
        return blogRepository.findAllBlogs(pageable)
                .map(blog -> modelMapper.map(blog, BlogDTO.class));
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

    @Override
    public PaginatedResponse<BlogDTO> createPaginatedResponse(Page<BlogDTO> blogPage) {
        return new PaginatedResponse<>(
                blogPage.getContent(),
                blogPage.getTotalPages(),
                blogPage.getTotalElements(),
                blogPage.getNumber(),
                blogPage.getSize()
        );
    }



}

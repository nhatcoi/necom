package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.BlogDTO;
import com.example.ecommerceweb.dto.PaginatedResponse;
import com.example.ecommerceweb.service.BlogCategoryService;
import com.example.ecommerceweb.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("${api.prefix}/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogCategoryService blogCategoryService;
    private final BlogService blogService;

    @GetMapping("")
    public String blog() {
        return "blog";
    }

    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<?>> getCategories() {
        List<?> categories = blogCategoryService.findAll();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/blogs")
    public ResponseEntity<PaginatedResponse<BlogDTO>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogDTO> blogPage = blogService.getAllBlogs(pageable);
        PaginatedResponse<BlogDTO> response = blogService.createPaginatedResponse(blogPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<PaginatedResponse<BlogDTO>> getBlogsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogDTO> blogPage = blogService.getBlogByCategory(pageable, id);
        PaginatedResponse<BlogDTO> response = blogService.createPaginatedResponse(blogPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-news")
    public ResponseEntity<List<BlogDTO>> getRecentNews(@RequestParam(defaultValue = "3") int size) {
        List<BlogDTO> blogs = blogService.getRecentBlogs(size);
        if (blogs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(blogs);
    }


}

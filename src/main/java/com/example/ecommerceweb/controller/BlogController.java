package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.BlogDTO;
import com.example.ecommerceweb.dto.PaginatedResponse;
import com.example.ecommerceweb.dto.response.Pagination;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.service.BlogCategoryService;
import com.example.ecommerceweb.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogCategoryService blogCategoryService;
    private final BlogService blogService;
    private final Translator translator;

    @GetMapping("/categories")
    public ResponseData<List<?>> getCategories() {
        List<?> categories = blogCategoryService.findAll();
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response success"), categories);
    }

    @GetMapping("/blogs")
    public ResponseData<?> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogDTO> blogPage = blogService.getAllBlogs(pageable);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("blogs.get.success"), blogPage.getContent(), new Pagination(blogPage));
    }

    @GetMapping("/categories/{id}")
    public ResponseData<?> getBlogsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogDTO> blogPage = blogService.getBlogByCategory(pageable, id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("blogs.category.get.success"), blogPage.getContent(), new Pagination(blogPage));
    }

    @GetMapping("/recent-news")
    public ResponseData<?> getRecentNews(@RequestParam(defaultValue = "3") int size) {
        List<BlogDTO> blogs = blogService.getRecentBlogs(size);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("blogs.get.success"), blogs);
    }

}

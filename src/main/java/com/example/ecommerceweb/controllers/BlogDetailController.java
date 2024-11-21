package com.example.ecommerceweb.controllers;

import com.example.ecommerceweb.dtos.BlogDTO;
import com.example.ecommerceweb.services.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("blog-details")
@RequiredArgsConstructor
public class BlogDetailController {
    private final BlogService blogService;


    @GetMapping("")
    public String blogDetail() {
        return "blog-details";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getBlogDetail(@PathVariable Long id) {
        BlogDTO blog = blogService.getBlogById(id);
        return ResponseEntity.ok().body(blog);
    }

}

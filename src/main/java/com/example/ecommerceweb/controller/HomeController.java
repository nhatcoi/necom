package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.CategoryDTO;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.service.CategoryService;
import com.example.ecommerceweb.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.ecommerceweb.util.DivideList.divideList;
import static com.example.ecommerceweb.util.Static.*;

@Slf4j
@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public String home(Model model) throws IOException, InterruptedException {
        List<CategoryDTO> categories = categoryService.getAllCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        List<List<ProductDTO>> latestProducts = divideList(
                productService.getLatestProducts(LATEST_LIMIT).stream()
                        .map(product -> modelMapper.map(product, ProductDTO.class))
                        .collect(Collectors.toList()),
                PAGE_SLIDE
        );
        List<List<ProductDTO>> topRatedProducts = divideList(
                productService.getTopRatedProducts(TOP_RATING_LIMIT).stream()
                        .map(product -> modelMapper.map(product, ProductDTO.class))
                        .collect(Collectors.toList()),
                PAGE_SLIDE
        );

        model.addAttribute("categories", categories);
        model.addAttribute("latestProducts", latestProducts);
        model.addAttribute("topRatedProducts", topRatedProducts);
        return "index";
    }

}


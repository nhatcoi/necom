package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.PaginatedResponse;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.service.services_impl.ProductImageService;
import com.example.ecommerceweb.service.services_impl.ProductRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RequestMapping("${api.prefix}/products")
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final ProductRatingService productRatingService;

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) throws IOException {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImageService.getProductImagesByProductId(id));
        model.addAttribute("avgRating", productRatingService.avgRating(id));
        return "shop-details";
    }

    @GetMapping("/product/share")
    public String getProductShare(@RequestParam Long id, Model model) {
        //get url product
        String urlProduct = ServletUriComponentsBuilder.fromCurrentContextPath().path("/products/").path(id.toString()).toUriString();
        model.addAttribute("urlProduct", urlProduct);
        return "common/share-product";
    }

    @GetMapping("")
    public ResponseEntity<PaginatedResponse<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productsPage = productService.getAllProducts(pageable);
        PaginatedResponse<ProductDTO> response = productService.createPaginatedResponse(productsPage);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/categories/{id}")
    public ResponseEntity<PaginatedResponse<ProductDTO>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productsPage = productService.getProductsByCategory(pageable, id);
        PaginatedResponse<ProductDTO> response = productService.createPaginatedResponse(productsPage);

        return ResponseEntity.ok(response);
    }

}
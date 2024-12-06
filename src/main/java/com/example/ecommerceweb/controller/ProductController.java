package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.PaginatedResponse;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.service.services_impl.ProductImageService;
import com.example.ecommerceweb.service.services_impl.ProductRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static com.example.ecommerceweb.util.DivideList.divideList;
import static com.example.ecommerceweb.util.Static.*;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final ProductRatingService productRatingService;
    private final Translator translator;

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


    @GetMapping("/category/{cateId}")
    public ResponseData<?> getProductsByCategory(
            @PathVariable Long cateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productsPage = productService.getProductsByCategory(pageable, cateId);
        List<ProductDTO> products = productsPage.getContent();

        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), products);
    }


    @GetMapping("/latest")
    public ResponseData<?> getLatestProducts() {
        List<List<ProductDTO>> latestProducts = divideList(
                productService.getLatestProducts(LATEST_LIMIT),
                PAGE_SLIDE
        );
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), latestProducts);
    }

    @GetMapping("/top-rated")
    public ResponseData<?> getTopRatedProducts() {
        List<List<ProductDTO>> topRatedProducts = divideList(
                productService.getTopRatedProducts(TOP_RATING_LIMIT),
                PAGE_SLIDE
        );
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), topRatedProducts);
    }

}
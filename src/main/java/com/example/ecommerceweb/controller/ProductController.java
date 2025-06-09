package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.product.ProductCreateRequest;
import com.example.ecommerceweb.dto.product.ProductDTO;
import com.example.ecommerceweb.dto.product.ProductFilterRequest;
import com.example.ecommerceweb.dto.response_data.Pagination;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import com.example.ecommerceweb.filter.ProductFilter;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.service.services_impl.ProductImageService;
import com.example.ecommerceweb.service.services_impl.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.ecommerceweb.specification.ProductSpecifications.resolveSort;
import static com.example.ecommerceweb.util.DivideList.divideList;
import static com.example.ecommerceweb.util.Static.*;

@Slf4j
@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final RatingService ratingService;
    private final Translator translator;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public ResponseData<?> getProducts(
            @ModelAttribute ProductFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Sort sort = resolveSort(filter.getSortBy());
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.getFilteredProducts(filter, pageable);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), products.getContent(), new Pagination(products));
    }

    @GetMapping("/ratings/average")
    public ResponseData<?> getAverageRating(@RequestParam(required = false) String ids) {
        List<Long> productIds = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        Map<Long, Double> averageRatings = ratingService.getAverageRatings(productIds);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), averageRatings);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getProductById(@PathVariable Long id) {
        ProductDetailResponse productDetailResponse = productService.getProductById(id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), productDetailResponse);
    }

    @GetMapping("/slug/{slug}")
    public ResponseData<?> getProductBySlug(@PathVariable String slug) {
        ProductDetailResponse productDetailResponse = productService.getProductBySlug(slug);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), productDetailResponse);
    }

    @GetMapping("/list-image/{id}")
    public ResponseData<?> getListImage(@PathVariable Long id) {
        // test
        List<String> response = productImageService.getImageListUrl(id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), response);
    }


    @PostMapping("")
    public ResponseData<?> createProduct(
            @RequestPart("data") String data,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> images
    ) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductCreateRequest request = objectMapper.convertValue(objectMapper.readTree(data), ProductCreateRequest.class);
        request.setThumbnail(thumbnail);
        request.setProductImages(images);

        log.info("Product request: " + request);
        log.info("Product thumbnail: " + thumbnail);
        log.info("Product images: " + images);

        productService.createProduct(request);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), null);
    }

    @DeleteMapping("/{productId}")
    public ResponseData<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), null);
    }



}
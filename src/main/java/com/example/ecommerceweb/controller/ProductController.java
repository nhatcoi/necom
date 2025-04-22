package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.product.ProductDTO;
import com.example.ecommerceweb.dto.response_data.Pagination;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import com.example.ecommerceweb.filter.ProductFilter;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.service.services_impl.ProductImageService;
import com.example.ecommerceweb.service.services_impl.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Boolean sortByNew,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Integer category
    ) {

        Pageable pageable = PageRequest.of(page, size);
        ProductFilter filter = new ProductFilter();
        filter.setSortByNew(sortByNew);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setInStock(inStock);
        filter.setCategory(category);

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

    @GetMapping("/new")
    public ResponseData<?> getNewProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getProducts(pageable);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), products.getContent(), new Pagination(products));
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

    @GetMapping("/filter-by-price")
    public ResponseData<?> filterProducts(
            @RequestParam(value = "minamount", required = false) String minAmount,
            @RequestParam(value = "maxamount", required = false) String maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        int min, max;
        try {
            min = minAmount != null ? Integer.parseInt(minAmount) : 0;
            max = maxAmount != null ? Integer.parseInt(maxAmount) : Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            min = 0;
            max = Integer.MAX_VALUE;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getProductByPriceRange(min, max, pageable);
        return new ResponseData<>(HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                products.getContent(),
                new Pagination(products));
    }


    @GetMapping("/search")
    public ResponseData<?> searchProducts(@RequestParam(value = "search", required = false) String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        String decodedSearch = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        Page<ProductDTO> products = productService.searchProducts(pageable, decodedSearch);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated(keyword + " search successful"), products.getContent(), new Pagination(products));
    }



}
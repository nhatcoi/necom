package com.example.ecommerceweb.controller;



import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.dto.response.product.ProductDetailResponse;
import com.example.ecommerceweb.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/product")
@RestController
@RequiredArgsConstructor
public class ShopDetailsController {

    private final ProductService productService;
    private final Translator translator;

    @GetMapping("/{id}")
    public ResponseData<?> getProductById(@PathVariable Long id) {
        ProductDetailResponse productDetailResponse = productService.getProductById(id);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), productDetailResponse);
    }
}
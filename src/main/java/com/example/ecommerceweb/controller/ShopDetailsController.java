package com.example.ecommerceweb.controller;



import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.service.services_impl.ProductImageService;
import com.example.ecommerceweb.service.services_impl.ProductRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/product-details")
@Controller
@RequiredArgsConstructor
public class ShopDetailsController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final ProductRatingService productRatingService;

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model)  {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImageService.getProductImagesByProductId(id));
        model.addAttribute("avgRating", productRatingService.avgRating(id));
        return "shop-details";
    }
}
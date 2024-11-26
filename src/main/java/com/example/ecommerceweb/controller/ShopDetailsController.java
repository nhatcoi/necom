package com.example.ecommerceweb.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${api.prefix}/shop-details")
@RequiredArgsConstructor
public class ShopDetailsController {

    @GetMapping("")
    public String shopDetail() {
        return "shop-details";
    }
}

package com.example.ecommerceweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class LayoutController {

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register-form")
    public String registerForm() {
        return "register";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }


    @GetMapping("/blog-details")
    public String blogDetail() {
        return "blog-details";
    }

    @GetMapping("/checkout")
    public String checkOut() {
        return "checkout";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/shop-details")
    public String shopDetail() {
        return "shop-details";
    }

    @GetMapping("/shopping-cart")
    public String shoppingCart() {
        return "shopping-cart";
    }

}

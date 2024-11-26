package com.example.ecommerceweb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class AuthLayoutController {

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register-form")
    public String registerForm() {
        return "register";
    }
}

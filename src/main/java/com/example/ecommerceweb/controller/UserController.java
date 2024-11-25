package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.response.UserResponse;
import com.example.ecommerceweb.service.UserService;
import com.example.ecommerceweb.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @GetMapping("users")
    @ResponseBody
    public ResponseEntity<?> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("Role: {}", grantedAuthority.getAuthority()));

        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("users/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping("/my-info")
    @ResponseBody
    public ResponseEntity<?> getMyInfo() {
        // only reference token can access this API
        return ResponseEntity.ok().body(userService.getMyInfo());
    }


    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity.ok().body(userResponse);
    }

}

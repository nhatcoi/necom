package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.response.UserResponse;
import com.example.ecommerceweb.service.UserService;
import com.example.ecommerceweb.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("users/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.getUserByPhoneNumberAndPassword(
                userRequest.getPhoneNumber(),
                userRequest.getPassword());
        return ResponseEntity.ok().body(userResponse);
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity.ok().body(userResponse);
    }

}

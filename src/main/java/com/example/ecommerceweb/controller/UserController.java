package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.dto.response.UserResponse;
import com.example.ecommerceweb.service.UserService;
import com.example.ecommerceweb.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final Translator translator;

    @GetMapping("/users")
    public ResponseData<?> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("Role: {}", grantedAuthority.getAuthority()));

        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseData<?> getUserById(@PathVariable Long userId) {
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getUserById(userId));
    }

    @GetMapping("/my-info")
    public ResponseData<?> getMyInfo() {
        // only reference token can access this API
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getMyInfo());
    }


    @PostMapping("/users")
    public ResponseData<?> register(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), translator.toLocated("response.success"), userResponse);
    }

}

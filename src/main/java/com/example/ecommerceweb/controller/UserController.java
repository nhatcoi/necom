package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.auth.PasswordDTO;
import com.example.ecommerceweb.dto.user.UserUpdateRequest;
import com.example.ecommerceweb.dto.response_data.Pagination;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.dto.user.UserResponse;
import com.example.ecommerceweb.service.UserService;
import com.example.ecommerceweb.dto.user.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final Translator translator;

    @GetMapping("")
    public ResponseData<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // how to get username and roles of current user from token SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info("Role: {}", grantedAuthority.getAuthority()));

        Page<UserResponse> users = userService.getUsers(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), users.getContent(), new Pagination(users));
    }

    @GetMapping("/{userId}")
    public ResponseData<?> getUserById(@PathVariable Long userId) {
        log.info("User: {}", userId);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getUserById(userId));
    }

//    @GetMapping("/{email}")
//    public ResponseData<?> getUserByEmail(@PathVariable String email) {
//        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getUserByEmail(email));
//    }

    @GetMapping("/search")
    public ResponseData<?> searchUsers(
            @RequestParam(value = "search", required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String decodedSearch = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        Page<UserResponse> users = userService.searchUsers(decodedSearch, page, size);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), users.getContent(), new Pagination(users));
    }

    @GetMapping("/my-info")
    public ResponseData<?> getMyInfo() {
        // only reference token can access this API
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userService.getMyInfo());
    }

    @PutMapping("/update-password")
    public ResponseData<?> updatePassword(@Valid @RequestBody PasswordDTO request) {
        userService.updateUserPassword(request);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), "OK");
    }


    @PostMapping("/create-user")
    public ResponseData<?> register(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), translator.toLocated("response.success"), userResponse);
    }

    @PutMapping("/update-profile")
    public ResponseData<?> updateProfile(@RequestBody UserRequest userUpdateRequest) {
        UserResponse userResponse = userService.updateUser(userUpdateRequest);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("Delete User Successfully"), "Delete User Successfully");
    }


    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse userResponse = userService.updateUserByAdmin(userId, userUpdateRequest);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), userResponse);
    }

}

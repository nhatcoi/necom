package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.request.user.UserRequest;
import com.example.ecommerceweb.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password);
    UserResponse createUser(UserRequest userRequest);
    Page<UserResponse> getUsers(int page, int size);
    UserResponse getUserById(Long userId);
    UserResponse getMyInfo();
}

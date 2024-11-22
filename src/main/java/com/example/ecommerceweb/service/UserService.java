package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.request.UserRequest;
import com.example.ecommerceweb.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password);
    UserResponse createUser(UserRequest userRequest);
    List<UserResponse> getUsers();
    UserResponse getUserById(Long userId);

}

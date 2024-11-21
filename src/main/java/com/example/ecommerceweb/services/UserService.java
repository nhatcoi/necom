package com.example.ecommerceweb.services;

import com.example.ecommerceweb.dtos.UserRequest;
import com.example.ecommerceweb.dtos.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password);
    UserResponse createUser(UserRequest userRequest);
    List<UserResponse> getUsers();
    UserResponse getUserById(Long userId);

}

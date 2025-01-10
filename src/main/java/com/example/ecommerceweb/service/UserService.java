package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.request.user.UserRequest;
import com.example.ecommerceweb.dto.request.user.UserUpdateRequest;
import com.example.ecommerceweb.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password);
    UserResponse createUser(UserRequest userRequest);
    Page<UserResponse> getUsers(int page, int size);
    UserResponse getUserById(Long userId);
    UserResponse getMyInfo();

    // delete user by id
    void deleteUser(Long userId);

    @PreAuthorize("hasRole('ADMIN')")
    UserResponse updateUserByAdmin(Long userId, UserUpdateRequest userUpdateRequest);

    Page<UserResponse> searchUsers(String decodedSearch, int page, int size);
}

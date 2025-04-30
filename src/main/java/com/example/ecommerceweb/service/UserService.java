package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.auth.PasswordDTO;
import com.example.ecommerceweb.dto.user.UserRequest;
import com.example.ecommerceweb.dto.user.UserUpdateRequest;
import com.example.ecommerceweb.dto.user.UserResponse;
import com.example.ecommerceweb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password);
    UserResponse createUser(UserRequest userRequest);
    Page<UserResponse> getUsers(int page, int size);
    UserResponse getUserById(Long userId);

    UserResponse updateUser(UserRequest userUpdateRequest);
    void updateUserPassword(PasswordDTO passwordDTO);

    UserResponse getMyInfo();

    // delete user by id
    void deleteUser(Long userId);

    UserResponse updateUserByAdmin(Long userId, UserUpdateRequest userUpdateRequest);

    Page<UserResponse> searchUsers(String decodedSearch, int page, int size);

    User getUserByUsername(String username);

    UserResponse getUserByEmail(String email);
}

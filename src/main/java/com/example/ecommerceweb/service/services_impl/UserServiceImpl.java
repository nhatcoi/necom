package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.UserRequest;
import com.example.ecommerceweb.dto.response.UserResponse;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceNotFoundException;
import com.example.ecommerceweb.mapper.UserMapper;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        if (user != null) {
            return userMapper.toUserResponse(user);
        }
        return null;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())
                || userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ResourceNotFoundException(ErrorCode.USER_EXISTED.getMessage());
        }

        Set<Role> roles = Set.of(
                Role.builder()
                        .id(RoleEnum.USER.getValue())
                        .name(RoleEnum.USER.name())
                        .build()
        );

        User user = userMapper.toUser(userRequest);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        context.getAuthentication().getName();

        User user = userRepository.findByUsername(context.getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> getUsers() {
        log.info("Get all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Get user by id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        return userMapper.toUserResponse(user);
    }
}

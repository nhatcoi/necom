package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.UserRequest;
import com.example.ecommerceweb.dto.response.UserResponse;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceNotFoundException;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        if (user != null) {
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .address(user.getAddress())
                    .build();
        }
        return null;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())
                || userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ResourceNotFoundException(ErrorCode.USER_EXISTED.getMessage());
        }

        Set<Role> roles = Set.of(Role.builder()
                .id(RoleEnum.USER.getValue())
                .name(RoleEnum.USER.name()).build());
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .phoneNumber(userRequest.getPhoneNumber())
                .fullName(userRequest.getFullName())
                .address(userRequest.getAddress())
                .dateOfBirth(userRequest.getDateOfBirth())
                .isActive(true)
                .roles(roles)
                .build();
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleNames(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .dateOfBirth(user.getDateOfBirth())
                        .roleNames(user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleNames(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}

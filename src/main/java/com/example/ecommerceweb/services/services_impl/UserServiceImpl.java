package com.example.ecommerceweb.services.services_impl;

import com.example.ecommerceweb.dtos.UserRequest;
import com.example.ecommerceweb.dtos.UserResponse;
import com.example.ecommerceweb.entities.Role;
import com.example.ecommerceweb.entities.User;
import com.example.ecommerceweb.exceptions.ResourceNotFoundException;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.services.UserService;
import com.google.firebase.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UserResponse getUserByPhoneNumberAndPassword(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumberAndPassword(phoneNumber, password);
        if (user != null) {
            return UserResponse.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .password(user.getPassword())
                    .address(user.getAddress())
                    .build();
        }
        return null;
    }


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new ResourceNotFoundException("Phone number already exists");
        }

        User user = User.builder()
                .phoneNumber(userRequest.getPhoneNumber())
                .password(userRequest.getPassword())
                .role(Role.builder().id(Long.valueOf(userRequest.getRoleId())).build())
                .build();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .build();
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .password(user.getPassword())
                        .address(user.getAddress())
                        .build())
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .address(user.getAddress())
                .build();
    }
}

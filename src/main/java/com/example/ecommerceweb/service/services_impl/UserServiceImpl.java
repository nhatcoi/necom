package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.user.UserRequest;
import com.example.ecommerceweb.dto.response.user.UserResponse;
import com.example.ecommerceweb.entity.Address;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.mapper.UserMapper;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            throw new ResourceException(ErrorCode.USER_EXISTED);
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
        user.setIsActive(true);

        List<Address> addresses = userRequest.getAddresses().stream()
                .map(addressRequest -> Address.builder()
                        .addressLine(addressRequest.getAddressLine())
                        .city(addressRequest.getCity())
                        .district(addressRequest.getDistrict())
                        .postcode(addressRequest.getPostcode())
                        .country(addressRequest.getCountry())
                        .user(user) // map to user
                        .build())
                .collect(Collectors.toList());
        user.setAddresses(addresses);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
        log.info(user.getEmail());
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<UserResponse> getUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users.map(userMapper::toUserResponse);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Get user by id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
}

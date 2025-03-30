package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.user.UserRequest;
import com.example.ecommerceweb.dto.request.user.UserUpdateRequest;
import com.example.ecommerceweb.dto.response.user.UserResponse;
import com.example.ecommerceweb.entity.Address;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.RoleEnum;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.mapper.UserMapper;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.security.SecurityUtils;
import com.example.ecommerceweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final SecurityUtils securityUtils;

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

        Set<Role> roles;
        if (userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
            roles = Set.of(
                    Role.builder()
                            .id(RoleEnum.USER.getValue())
                            .name(RoleEnum.USER.name())
                            .build()
            );
        } else {
            if (!securityUtils.hasAdminPermission()) {
                throw new ResourceException(ErrorCode.FORBIDDEN, "You can not set role for user");
            }
            roles = setRolesForUser(userRequest);
        }

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


    private @NotNull Set<Role> setRolesForUser(UserRequest userRequest) {
        return userRequest.getRoles().stream()
                .map(roleEnum -> Role.builder()
                        .id(roleEnum.getValue())
                        .name(roleEnum.name())
                        .build())
                .collect(Collectors.toSet());
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
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<UserResponse> getUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users.map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse updateUserByAdmin(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));

        if(userUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        if (userUpdateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        }
        if (userUpdateRequest.getFullName() != null) {
            user.setFullName(userUpdateRequest.getFullName());
        }
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        }
        if (userUpdateRequest.getIsActive() != null) {
            user.setIsActive(userUpdateRequest.getIsActive());
        }
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> searchUsers(String decodedSearch, int page, int size) {
        Page<User> users = userRepository.searchUsers(PageRequest.of(page, size), decodedSearch);
        return users.map(userMapper::toUserResponse);
    }

}

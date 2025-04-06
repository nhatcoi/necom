package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.PasswordDTO;
import com.example.ecommerceweb.dto.request.user.AddressRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ResourceException(ErrorCode.USER_EXISTED);
        }

        if (CollectionUtils.isEmpty(userRequest.getRoles())) {
            userRequest.setRoles(Set.of(RoleEnum.USER));
        } else {
            if (!securityUtils.hasAdminPermission()) {
                throw new ResourceException(ErrorCode.FORBIDDEN, "You can not set role for user");
            }
        }

        User user;
        user = userMapper.toUser(userRequest);
        user.setUsername(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAddresses(mapAddresses(userRequest.getAddresses(), user));
        user.setIsActive(true);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserRequest userUpdateRequest) {
        User user = securityUtils.getCurrentUser();
        Optional.ofNullable(userUpdateRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(userUpdateRequest.getFullName()).ifPresent(user::setFullName);
        Optional.ofNullable(userUpdateRequest.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userUpdateRequest.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userUpdateRequest.getAddresses())
                .ifPresent(addresses -> user.setAddresses(mapAddresses(addresses, user)));

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    // common for update Address
    private List<Address> mapAddresses(List<AddressRequest> addressRequests, User user) {
        return Optional.ofNullable(addressRequests)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(addressRequest -> Address.builder()
                        .id(addressRequest.getId())
                        .addressLine(addressRequest.getAddressLine())
                        .city(addressRequest.getCity())
                        .district(addressRequest.getDistrict())
                        .postcode(addressRequest.getPostcode())
                        .country(addressRequest.getCountry())
                        .user(user)
                        .isMain(addressRequest.getIsMain())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserPassword(PasswordDTO passwordDTO) {
        User user = securityUtils.getCurrentUser();
        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new ResourceException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }


    @Override
    public UserResponse getMyInfo() {
        User user = securityUtils.getCurrentUser();
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

        Optional.ofNullable(userUpdateRequest.getPassword())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(userUpdateRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(userUpdateRequest.getFullName()).ifPresent(user::setFullName);
        Optional.ofNullable(userUpdateRequest.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userUpdateRequest.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userUpdateRequest.getIsActive()).ifPresent(user::setIsActive);

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }


    @Override
    public Page<UserResponse> searchUsers(String decodedSearch, int page, int size) {
        Page<User> users = userRepository.searchUsers(PageRequest.of(page, size), decodedSearch);
        return users.map(userMapper::toUserResponse);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
    }

}

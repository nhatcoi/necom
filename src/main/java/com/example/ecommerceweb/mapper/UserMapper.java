package com.example.ecommerceweb.mapper;

import com.example.ecommerceweb.dto.request.user.UserRequest;
import com.example.ecommerceweb.dto.response.user.UserResponse;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "mapRolesToNames")
    @Mapping(target = "email", source = "email") // if add new field
    @Mapping(target = "isActive", source = "isActive") // if add new field
    UserResponse toUserResponse(User user);

    User toUser(UserRequest request);

    @Named("mapRolesToNames")
    static Set<String> mapRolesToNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}

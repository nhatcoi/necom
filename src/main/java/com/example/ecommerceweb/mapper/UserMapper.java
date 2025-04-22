package com.example.ecommerceweb.mapper;

import com.example.ecommerceweb.dto.user.UserRequest;
import com.example.ecommerceweb.dto.user.AddressResponse;
import com.example.ecommerceweb.dto.user.UserResponse;
import com.example.ecommerceweb.entity.Address;
import com.example.ecommerceweb.entity.Role;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "mapRolesToNames")
    @Mapping(target = "addresses", source = "addresses", qualifiedByName = "mapAddressesToResponses")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoleEnumsToRoles")
    User toUser(UserRequest request);

    @Named("mapRolesToNames")
    static Set<String> mapRolesToNames(Set<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("mapRoleEnumsToRoles")
    static Set<Role> mapRoleEnumsToRoles(Set<RoleEnum> roleEnums) {
        if (CollectionUtils.isEmpty(roleEnums)) return Collections.emptySet();
        return roleEnums.stream()
                .map(roleEnum -> Role.builder()
                        .id(roleEnum.getValue())
                        .name(roleEnum.name())
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("mapAddressesToResponses")
    static List<AddressResponse> mapAddressesToResponses(List<Address> addresses) {
        if (CollectionUtils.isEmpty(addresses)) {
            return Collections.emptyList();
        }
        return addresses.stream()
                .map(address -> AddressResponse.builder()
                        .id(address.getId())
                        .addressLine(address.getAddressLine())
                        .city(address.getCity())
                        .district(address.getDistrict())
                        .postcode(address.getPostcode())
                        .country(address.getCountry())
                        .isMain(address.getIsMain())
                        .build())
                .collect(Collectors.toList());
    }
}

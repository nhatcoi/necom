package com.necom.mapper.authentication;

import com.necom.dto.authentication.UserRequest;
import com.necom.dto.authentication.UserResponse;
import com.necom.dto.client.ClientEmailSettingUserRequest;
import com.necom.dto.client.ClientPersonalSettingUserRequest;
import com.necom.dto.client.ClientPhoneSettingUserRequest;
import com.necom.entity.authentication.User;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.address.AddressMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {MapperUtils.class, AddressMapper.class})
public interface UserMapper extends GenericMapper<User, UserRequest, UserResponse> {

    @Override
    @BeanMapping(qualifiedByName = "attachUser")
    @Mapping(source = "password", target = "password", qualifiedByName = "hashPassword")
    User requestToEntity(UserRequest request);

    @Override
    @BeanMapping(qualifiedByName = "attachUser")
    @Mapping(source = "password", target = "password", qualifiedByName = "hashPassword",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(@MappingTarget User entity, UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(@MappingTarget User entity, ClientPersonalSettingUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(@MappingTarget User entity, ClientPhoneSettingUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(@MappingTarget User entity, ClientEmailSettingUserRequest request);

}

package com.necom.mapper.authentication;

import com.necom.dto.authentication.RoleRequest;
import com.necom.dto.authentication.RoleResponse;
import com.necom.entity.authentication.Role;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GenericMapper<Role, RoleRequest, RoleResponse> {
}

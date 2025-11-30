package com.necom.mapper.customer;

import com.necom.dto.customer.CustomerRequest;
import com.necom.dto.customer.CustomerResponse;
import com.necom.entity.customer.Customer;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.authentication.UserMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {MapperUtils.class, UserMapper.class})
public interface CustomerMapper extends GenericMapper<Customer, CustomerRequest, CustomerResponse> {

    @Override
    @Mapping(source = "customerGroupId", target = "customerGroup")
    @Mapping(source = "customerResourceId", target = "customerResource")
    @Mapping(source = "customerStatusId", target = "customerStatus")
    Customer requestToEntity(CustomerRequest request);

    @Override
    @Mapping(source = "customerGroupId", target = "customerGroup")
    @Mapping(source = "customerResourceId", target = "customerResource")
    @Mapping(source = "customerStatusId", target = "customerStatus")
    Customer partialUpdate(@MappingTarget Customer entity, CustomerRequest request);

}

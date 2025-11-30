package com.necom.mapper.customer;

import com.necom.dto.customer.CustomerGroupRequest;
import com.necom.dto.customer.CustomerGroupResponse;
import com.necom.entity.customer.CustomerGroup;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerGroupMapper extends GenericMapper<CustomerGroup, CustomerGroupRequest, CustomerGroupResponse> {
}

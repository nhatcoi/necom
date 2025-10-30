package com.necom.mapper.customer;

import com.necom.dto.customer.CustomerResourceRequest;
import com.necom.dto.customer.CustomerResourceResponse;
import com.necom.entity.customer.CustomerResource;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerResourceMapper extends GenericMapper<CustomerResource, CustomerResourceRequest, CustomerResourceResponse> {
}

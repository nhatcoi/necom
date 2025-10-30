package com.necom.mapper.customer;

import com.necom.dto.customer.CustomerStatusRequest;
import com.necom.dto.customer.CustomerStatusResponse;
import com.necom.entity.customer.CustomerStatus;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerStatusMapper extends GenericMapper<CustomerStatus, CustomerStatusRequest, CustomerStatusResponse> {
}

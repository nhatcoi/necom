package com.necom.mapper.employee;

import com.necom.dto.employee.OfficeRequest;
import com.necom.dto.employee.OfficeResponse;
import com.necom.entity.employee.Office;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AddressMapper.class)
public interface OfficeMapper extends GenericMapper<Office, OfficeRequest, OfficeResponse> {
}

package com.necom.mapper.product;

import com.necom.dto.product.PropertyRequest;
import com.necom.dto.product.PropertyResponse;
import com.necom.entity.product.Property;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PropertyMapper extends GenericMapper<Property, PropertyRequest, PropertyResponse> {
}

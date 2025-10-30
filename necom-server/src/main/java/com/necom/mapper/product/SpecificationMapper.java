package com.necom.mapper.product;

import com.necom.dto.product.SpecificationRequest;
import com.necom.dto.product.SpecificationResponse;
import com.necom.entity.product.Specification;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpecificationMapper extends GenericMapper<Specification, SpecificationRequest, SpecificationResponse> {
}

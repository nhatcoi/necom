package com.necom.mapper.product;

import com.necom.dto.product.VariantRequest;
import com.necom.dto.product.VariantResponse;
import com.necom.entity.product.Variant;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VariantMapper extends GenericMapper<Variant, VariantRequest, VariantResponse> {
}

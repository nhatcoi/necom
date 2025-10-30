package com.necom.mapper.product;

import com.necom.dto.product.BrandRequest;
import com.necom.dto.product.BrandResponse;
import com.necom.entity.product.Brand;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper extends GenericMapper<Brand, BrandRequest, BrandResponse> {}

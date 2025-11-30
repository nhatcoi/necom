package com.necom.mapper.product;

import com.necom.dto.product.UnitRequest;
import com.necom.dto.product.UnitResponse;
import com.necom.entity.product.Unit;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UnitMapper extends GenericMapper<Unit, UnitRequest, UnitResponse> {
}

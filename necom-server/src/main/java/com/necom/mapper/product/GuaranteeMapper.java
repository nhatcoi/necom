package com.necom.mapper.product;

import com.necom.dto.product.GuaranteeRequest;
import com.necom.dto.product.GuaranteeResponse;
import com.necom.entity.product.Guarantee;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuaranteeMapper extends GenericMapper<Guarantee, GuaranteeRequest, GuaranteeResponse> {
}

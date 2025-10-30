package com.necom.mapper.inventory;

import com.necom.dto.inventory.DocketReasonRequest;
import com.necom.dto.inventory.DocketReasonResponse;
import com.necom.entity.inventory.DocketReason;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocketReasonMapper extends GenericMapper<DocketReason, DocketReasonRequest, DocketReasonResponse> {}

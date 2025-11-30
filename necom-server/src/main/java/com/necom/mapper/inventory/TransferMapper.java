package com.necom.mapper.inventory;

import com.necom.dto.inventory.TransferRequest;
import com.necom.dto.inventory.TransferResponse;
import com.necom.entity.inventory.Transfer;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = DocketMapper.class)
public interface TransferMapper extends GenericMapper<Transfer, TransferRequest, TransferResponse> {}

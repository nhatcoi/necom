package com.necom.mapper.inventory;

import com.necom.dto.inventory.DestinationRequest;
import com.necom.dto.inventory.DestinationResponse;
import com.necom.entity.inventory.Destination;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AddressMapper.class)
public interface DestinationMapper extends GenericMapper<Destination, DestinationRequest, DestinationResponse> {}

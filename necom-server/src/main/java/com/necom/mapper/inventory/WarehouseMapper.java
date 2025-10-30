package com.necom.mapper.inventory;

import com.necom.dto.inventory.WarehouseRequest;
import com.necom.dto.inventory.WarehouseResponse;
import com.necom.entity.inventory.Warehouse;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AddressMapper.class)
public interface WarehouseMapper extends GenericMapper<Warehouse, WarehouseRequest, WarehouseResponse> {}

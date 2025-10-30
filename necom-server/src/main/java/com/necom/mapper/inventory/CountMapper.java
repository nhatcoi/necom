package com.necom.mapper.inventory;

import com.necom.dto.inventory.CountRequest;
import com.necom.dto.inventory.CountResponse;
import com.necom.entity.inventory.Count;
import com.necom.mapper.GenericMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MapperUtils.class, WarehouseMapper.class, CountVariantMapper.class})
public interface CountMapper extends GenericMapper<Count, CountRequest, CountResponse> {

    @Override
    @BeanMapping(qualifiedByName = "attachCount")
    @Mapping(source = "warehouseId", target = "warehouse")
    Count requestToEntity(CountRequest request);

    @Override
    @BeanMapping(qualifiedByName = "attachCount")
    @Mapping(source = "warehouseId", target = "warehouse")
    Count partialUpdate(@MappingTarget Count entity, CountRequest request);

}

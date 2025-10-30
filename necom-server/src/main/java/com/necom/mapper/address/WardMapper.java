package com.necom.mapper.address;

import com.necom.dto.address.WardRequest;
import com.necom.dto.address.WardResponse;
import com.necom.entity.address.Ward;
import com.necom.mapper.GenericMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperUtils.class)
public interface WardMapper extends GenericMapper<Ward, WardRequest, WardResponse> {

    @Override
    @Mapping(source = "districtId", target = "district")
    Ward requestToEntity(WardRequest request);

    @Override
    @Mapping(source = "districtId", target = "district")
    Ward partialUpdate(@MappingTarget Ward entity, WardRequest request);

}

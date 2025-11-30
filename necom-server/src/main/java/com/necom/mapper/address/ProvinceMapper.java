package com.necom.mapper.address;

import com.necom.dto.address.ProvinceRequest;
import com.necom.dto.address.ProvinceResponse;
import com.necom.entity.address.Province;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProvinceMapper extends GenericMapper<Province, ProvinceRequest, ProvinceResponse> {
}

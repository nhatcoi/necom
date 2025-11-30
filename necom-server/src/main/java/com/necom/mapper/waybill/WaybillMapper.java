package com.necom.mapper.waybill;

import com.necom.dto.waybill.WaybillRequest;
import com.necom.dto.waybill.WaybillResponse;
import com.necom.entity.waybill.Waybill;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.order.OrderMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {MapperUtils.class, OrderMapper.class})
public interface WaybillMapper extends GenericMapper<Waybill, WaybillRequest, WaybillResponse> {

    @Override
    @Mapping(source = "orderId", target = "order")
    Waybill requestToEntity(WaybillRequest request);

    @Override
    @Mapping(source = "orderId", target = "order")
    Waybill partialUpdate(@MappingTarget Waybill entity, WaybillRequest request);

}

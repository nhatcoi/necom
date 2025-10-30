package com.necom.mapper.order;

import com.necom.dto.order.OrderCancellationReasonRequest;
import com.necom.dto.order.OrderCancellationReasonResponse;
import com.necom.entity.order.OrderCancellationReason;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderCancellationReasonMapper extends GenericMapper<OrderCancellationReason, OrderCancellationReasonRequest,
        OrderCancellationReasonResponse> {}

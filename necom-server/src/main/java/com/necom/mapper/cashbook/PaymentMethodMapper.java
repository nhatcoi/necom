package com.necom.mapper.cashbook;

import com.necom.dto.cashbook.ClientPaymentMethodResponse;
import com.necom.dto.cashbook.PaymentMethodRequest;
import com.necom.dto.cashbook.PaymentMethodResponse;
import com.necom.entity.cashbook.PaymentMethod;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMethodMapper extends GenericMapper<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse> {

    @Mapping(source = "id", target = "paymentMethodId")
    @Mapping(source = "name", target = "paymentMethodName")
    @Mapping(source = "code", target = "paymentMethodCode")
    ClientPaymentMethodResponse entityToClientResponse(PaymentMethod entity);

    List<ClientPaymentMethodResponse> entityToClientResponse(List<PaymentMethod> entities);

}

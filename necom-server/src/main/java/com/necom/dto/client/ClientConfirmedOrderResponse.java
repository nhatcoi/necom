package com.necom.dto.client;

import com.necom.entity.cashbook.PaymentMethodType;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ClientConfirmedOrderResponse {
    private String orderCode;
    private PaymentMethodType orderPaymentMethodType;
    @Nullable
    private String orderPaypalCheckoutLink;
}

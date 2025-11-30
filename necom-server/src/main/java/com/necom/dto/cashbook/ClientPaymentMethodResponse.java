package com.necom.dto.cashbook;

import com.necom.entity.cashbook.PaymentMethodType;
import lombok.Data;

@Data
public class ClientPaymentMethodResponse {
    private Long paymentMethodId;
    private String paymentMethodName;
    private PaymentMethodType paymentMethodCode;
}

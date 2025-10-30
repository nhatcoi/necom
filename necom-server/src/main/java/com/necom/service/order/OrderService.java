package com.necom.service.order;

import com.necom.dto.client.ClientConfirmedOrderResponse;
import com.necom.dto.client.ClientSimpleOrderRequest;

public interface OrderService {

    void cancelOrder(String code);

    ClientConfirmedOrderResponse createClientOrder(ClientSimpleOrderRequest request);

    void captureTransactionPaypal(String paypalOrderId, String payerId);

}

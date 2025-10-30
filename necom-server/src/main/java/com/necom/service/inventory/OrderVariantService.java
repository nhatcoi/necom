package com.necom.service.inventory;

import com.necom.dto.order.OrderVariantRequest;
import com.necom.dto.order.OrderVariantResponse;
import com.necom.entity.order.OrderVariantKey;
import com.necom.service.CrudService;

public interface OrderVariantService extends CrudService<OrderVariantKey, OrderVariantRequest, OrderVariantResponse> {}

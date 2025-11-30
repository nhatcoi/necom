package com.necom.service.inventory;

import com.necom.dto.inventory.PurchaseOrderVariantRequest;
import com.necom.dto.inventory.PurchaseOrderVariantResponse;
import com.necom.entity.inventory.PurchaseOrderVariantKey;
import com.necom.service.CrudService;

public interface PurchaseOrderVariantService extends CrudService<PurchaseOrderVariantKey, PurchaseOrderVariantRequest,
        PurchaseOrderVariantResponse> {}

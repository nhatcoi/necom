package com.necom.dto.inventory;

import lombok.Data;

@Data
public class PurchaseOrderVariantKeyRequest {
    private Long purchaseOrderId;
    private Long variantId;
}

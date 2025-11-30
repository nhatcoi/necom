package com.necom.dto.inventory;

import lombok.Data;

@Data
public class DocketVariantKeyRequest {
    private Long docketId;
    private Long variantId;
}

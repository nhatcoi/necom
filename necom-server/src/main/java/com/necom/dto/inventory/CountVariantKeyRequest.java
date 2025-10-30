package com.necom.dto.inventory;

import lombok.Data;

@Data
public class CountVariantKeyRequest {
    private Long countId;
    private Long variantId;
}

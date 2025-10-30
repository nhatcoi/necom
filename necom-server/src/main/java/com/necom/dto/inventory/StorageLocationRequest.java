package com.necom.dto.inventory;

import lombok.Data;

@Data
public class StorageLocationRequest {
    private Long warehouseId;
    private Long variantId;
    private String name;
}

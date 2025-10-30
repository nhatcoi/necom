package com.necom.dto.inventory;

import com.necom.dto.product.VariantResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class StorageLocationResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private WarehouseResponse warehouse;
    private VariantResponse variant;
    private String name;
}

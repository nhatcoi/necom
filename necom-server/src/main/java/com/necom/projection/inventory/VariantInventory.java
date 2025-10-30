package com.necom.projection.inventory;

import com.necom.entity.inventory.DocketVariant;
import com.necom.entity.product.Variant;
import lombok.Data;

import java.util.List;

@Data
public class VariantInventory {
    private Variant variant;
    private List<DocketVariant> transactions;
    private Integer inventory;
    private Integer waitingForDelivery;
    private Integer canBeSold;
    private Integer areComing;
}

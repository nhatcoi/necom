package com.necom.projection.inventory;

import lombok.Data;

@Data
public class SimpleProductInventory {
    private Long productId;
    private Integer inventory;
    private Integer waitingForDelivery;
    private Integer canBeSold;
    private Integer areComing;

    // Nhận Number: Hibernate 6 trả Integer cho biểu thức SUM/CASE trên cột INT (Hibernate 5 trả Long)
    public SimpleProductInventory(
            Long productId,
            Number inventory,
            Number waitingForDelivery,
            Number canBeSold,
            Number areComing
    ) {
        this.productId = productId;
        this.inventory = toInt(inventory);
        this.waitingForDelivery = toInt(waitingForDelivery);
        this.canBeSold = toInt(canBeSold);
        this.areComing = toInt(areComing);
    }

    private static Integer toInt(Number value) {
        return value == null ? 0 : Math.toIntExact(value.longValue());
    }
}

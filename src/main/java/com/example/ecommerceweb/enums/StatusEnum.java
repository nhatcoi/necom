package com.example.ecommerceweb.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    INITIATED("Order has been created, awaiting further action"),
    PENDING_PAYMENT("Awaiting payment from the customer"),
    SUCCESS("Payment completed successfully"),
    PAYMENT_FAILED("Payment failed"),
    CANCELED("Order has been canceled"),
    REFUNDED("Order has been refunded"),
    PAID("Order has been paid");

    private final String description;

    StatusEnum(String description) {
        this.description = description;
    }

    public static StatusEnum fromString(String status) {
        for (StatusEnum orderStatus : StatusEnum.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}

package com.example.ecommerceweb.enums;

public enum RoleEnum {
    ADMIN(1),
    STAFF(2),
    USER(3);

    private final int value;

    RoleEnum(int value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}

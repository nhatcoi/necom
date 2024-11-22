package com.example.ecommerceweb.enums;

public enum Role {
    ADMIN(1),
    STAFF(2),
    USER(3);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}

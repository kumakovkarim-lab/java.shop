package com.warehouse.model;

import java.math.BigDecimal;

public enum DeliveryMethod {
    PICKUP(BigDecimal.ZERO),
    COURIER(new BigDecimal("5.00")),
    EXPRESS(new BigDecimal("12.00"));

    private final BigDecimal fee;

    DeliveryMethod(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }
}

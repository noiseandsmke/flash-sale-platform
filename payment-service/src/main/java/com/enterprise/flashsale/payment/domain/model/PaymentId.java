package com.enterprise.flashsale.payment.domain.model;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
    public PaymentId {
        Objects.requireNonNull(value, "Payment ID value must not be null");
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId fromString(String value) {
        Objects.requireNonNull(value, "Payment ID string must not be null");
        return new PaymentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

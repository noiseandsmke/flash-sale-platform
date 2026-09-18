package com.enterprise.flashsale.payment.domain.model;

import com.enterprise.flashsale.payment.domain.exception.InvalidPaymentStateException;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED;

    public PaymentStatus transitionTo(PaymentStatus nextStatus) {
        return switch (this) {
            case PENDING -> {
                if (nextStatus == SUCCESS || nextStatus == FAILED) {
                    yield nextStatus;
                }
                throw new InvalidPaymentStateException("Pending payment can only transition to SUCCESS or FAILED");
            }
            case SUCCESS ->
                throw new InvalidPaymentStateException("Successful payment cannot transition to any other status");
            case FAILED ->
                throw new InvalidPaymentStateException("Failed payment cannot transition to any other status");
        };
    }
}

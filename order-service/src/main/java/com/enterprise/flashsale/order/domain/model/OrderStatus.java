package com.enterprise.flashsale.order.domain.model;

import com.enterprise.flashsale.order.domain.exception.InvalidOrderStateException;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    CANCELLED;

    public OrderStatus transitionTo(OrderStatus nextStatus) {
        return switch (this) {
            case PENDING_PAYMENT -> {
                if (nextStatus == PAID || nextStatus == CANCELLED) {
                    yield nextStatus;
                }
                throw new InvalidOrderStateException("Pending payment order can only transition to PAID or CANCELLED");
            }
            case PAID -> throw new InvalidOrderStateException("Paid order cannot transition to any other status");
            case CANCELLED ->
                throw new InvalidOrderStateException("Cancelled order cannot transition to any other status");
        };
    }
}

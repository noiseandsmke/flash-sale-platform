package com.enterprise.flashsale.reservation.domain.model;

public enum TicketStatus {
    AVAILABLE,
    RESERVED,
    SOLD;

    public TicketStatus transitionTo(TicketStatus nextStatus) {
        return switch (this) {
            case AVAILABLE -> {
                if (nextStatus == RESERVED) {
                    yield RESERVED;
                }
                throw new IllegalStateException("Available ticket can only transition to RESERVED");
            }
            case RESERVED -> {
                if (nextStatus == SOLD) {
                    yield SOLD;
                }
                if (nextStatus == AVAILABLE) {
                    yield AVAILABLE;
                }
                throw new IllegalStateException("Reserved ticket can only transition to SOLD or AVAILABLE");
            }
            case SOLD -> throw new IllegalStateException("Sold ticket cannot transition to any other status");
        };
    }
}

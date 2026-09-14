package com.enterprise.flashsale.reservation.domain.model;

import java.util.Objects;

public record TicketId(Long value) {
    public TicketId {
        Objects.requireNonNull(value, "Ticket ID value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Ticket ID must be a strictly positive integer");
        }
    }

    public static TicketId of(Long value) {
        return new TicketId(value);
    }
}

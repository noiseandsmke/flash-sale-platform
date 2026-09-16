package com.enterprise.flashsale.order.domain.model;

import java.util.Objects;

public record OrderItem(Long ticketId, Long eventId, Money price) {
    public OrderItem(Long ticketId, Long eventId, Money price) {
        this.ticketId = Objects.requireNonNull(ticketId, "Ticket ID must not be null");
        this.eventId = Objects.requireNonNull(eventId, "Event ID must not be null");
        this.price = Objects.requireNonNull(price, "Price must not be null");

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Ticket ID must be positive");
        }
        if (eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be positive");
        }
    }
}

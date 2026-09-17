package com.enterprise.flashsale.order.application.port.in;

import com.enterprise.flashsale.order.domain.model.Money;

import java.util.Objects;
import java.util.UUID;

public record CreateOrderCommand(UUID eventId, Long ticketId, Long eventIdRef, String userId, Money price) {
    public CreateOrderCommand {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(ticketId, "Ticket ID must not be null");
        Objects.requireNonNull(eventIdRef, "Event reference ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(price, "Price must not be null");

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Ticket ID must be positive");
        }
        if (eventIdRef <= 0) {
            throw new IllegalArgumentException("Event reference ID must be positive");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }
    }
}

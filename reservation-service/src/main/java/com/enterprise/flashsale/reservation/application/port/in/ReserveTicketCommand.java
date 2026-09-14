package com.enterprise.flashsale.reservation.application.port.in;

import java.util.Objects;

public record ReserveTicketCommand(Long eventId, Long ticketId, String userId) {
    public ReserveTicketCommand {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(ticketId, "Ticket ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");

        if (eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be a strictly positive integer");
        }
        if (ticketId <= 0) {
            throw new IllegalArgumentException("Ticket ID must be a strictly positive integer");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }
    }

    public static ReserveTicketCommand of(Long eventId, Long ticketId, String userId) {
        return new ReserveTicketCommand(eventId, ticketId, userId);
    }
}

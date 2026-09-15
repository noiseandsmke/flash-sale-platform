package com.enterprise.flashsale.reservation.domain.event;

import com.enterprise.flashsale.reservation.domain.model.TicketId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record TicketReservedEvent(
        UUID eventId, Instant occurredOn, String aggregateId, Long eventIdRef, String userId, Instant expiresAt)
        implements DomainEvent {
    public TicketReservedEvent {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(occurredOn, "Occurred timestamp must not be null");
        Objects.requireNonNull(aggregateId, "Aggregate ID must not be null");
        Objects.requireNonNull(eventIdRef, "Event reference ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(expiresAt, "Expiration timestamp must not be null");
    }

    public static TicketReservedEvent create(TicketId ticketId, Long eventIdRef, String userId, Instant expiresAt) {
        return new TicketReservedEvent(
                UUID.randomUUID(), Instant.now(), String.valueOf(ticketId.value()), eventIdRef, userId, expiresAt);
    }

    @Override
    public String eventType() {
        return "TICKET_RESERVED";
    }
}

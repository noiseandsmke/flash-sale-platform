package com.enterprise.flashsale.order.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId, Instant occurredOn, String aggregateId, String userId, List<Long> ticketIds, String reason)
        implements DomainEvent {
    public OrderCancelledEvent {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(occurredOn, "Occurred timestamp must not be null");
        Objects.requireNonNull(aggregateId, "Aggregate ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(ticketIds, "Ticket IDs must not be null");
    }

    public static OrderCancelledEvent of(String orderId, String userId, List<Long> ticketIds, String reason) {
        return new OrderCancelledEvent(UUID.randomUUID(), Instant.now(), orderId, userId, ticketIds, reason);
    }

    @Override
    public String eventType() {
        return "ORDER_CANCELLED";
    }
}

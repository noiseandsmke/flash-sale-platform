package com.enterprise.flashsale.order.domain.event;

import com.enterprise.flashsale.order.domain.model.Money;
import com.enterprise.flashsale.order.domain.model.OrderId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderCreatedEvent(UUID eventId, Instant occurredOn, String aggregateId, String userId, Money totalAmount)
        implements DomainEvent {
    public OrderCreatedEvent {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(occurredOn, "Occurred timestamp must not be null");
        Objects.requireNonNull(aggregateId, "Aggregate ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(totalAmount, "Total amount must not be null");
    }

    public static OrderCreatedEvent from(OrderId orderId, String userId, Money totalAmount) {
        return new OrderCreatedEvent(UUID.randomUUID(), Instant.now(), orderId.toString(), userId, totalAmount);
    }

    @Override
    public String eventType() {
        return "ORDER_CREATED";
    }
}

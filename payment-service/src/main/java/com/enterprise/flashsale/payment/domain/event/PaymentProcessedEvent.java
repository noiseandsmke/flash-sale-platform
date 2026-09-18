package com.enterprise.flashsale.payment.domain.event;

import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PaymentProcessedEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        String orderId,
        String userId,
        Money amount,
        String transactionReference)
        implements DomainEvent {

    public PaymentProcessedEvent {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(occurredOn, "Occurred timestamp must not be null");
        Objects.requireNonNull(aggregateId, "Aggregate ID must not be null");
        Objects.requireNonNull(orderId, "Order ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
    }

    public static PaymentProcessedEvent from(
            PaymentId paymentId, String orderId, String userId, Money amount, String transactionReference) {
        return new PaymentProcessedEvent(
                UUID.randomUUID(), Instant.now(), paymentId.toString(), orderId, userId, amount, transactionReference);
    }

    @Override
    public String eventType() {
        return "PAYMENT_PROCESSED";
    }
}

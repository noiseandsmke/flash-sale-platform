package com.enterprise.flashsale.payment.adapter.in.kafka.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedMessage(
        UUID eventId, Instant occurredOn, String aggregateId, String userId, AmountDto totalAmount) {
    public record AmountDto(BigDecimal amount, String currency) {}
}

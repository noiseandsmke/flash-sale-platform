package com.enterprise.flashsale.order.adapter.in.kafka.dto;

import java.time.Instant;
import java.util.UUID;

public record TicketReservedMessage(
        UUID eventId, Instant occurredOn, String aggregateId, Long eventIdRef, String userId, Instant expiresAt) {}

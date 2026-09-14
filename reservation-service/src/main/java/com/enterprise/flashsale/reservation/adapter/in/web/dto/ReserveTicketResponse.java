package com.enterprise.flashsale.reservation.adapter.in.web.dto;

import java.time.Instant;

public record ReserveTicketResponse(Long ticketId, String userId, String status, Instant expiresAt) {
    public static ReserveTicketResponse of(Long ticketId, String userId, Instant expiresAt) {
        return new ReserveTicketResponse(ticketId, userId, "RESERVED", expiresAt);
    }
}

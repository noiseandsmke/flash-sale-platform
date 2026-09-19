package com.enterprise.flashsale.reservation.application.port.out;

import com.enterprise.flashsale.reservation.domain.model.TicketId;

public interface TicketInventoryPort {
    boolean reserveStock(Long eventId, TicketId ticketId, String userId, long ttlSeconds);

    boolean releaseStock(TicketId ticketId, String userId);
}

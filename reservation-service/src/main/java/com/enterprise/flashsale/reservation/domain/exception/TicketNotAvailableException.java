package com.enterprise.flashsale.reservation.domain.exception;

import com.enterprise.flashsale.reservation.domain.model.TicketId;

public class TicketNotAvailableException extends DomainException {
    private static final String ERROR_CODE = "TICKET_NOT_AVAILABLE";
    private final TicketId ticketId;

    public TicketNotAvailableException(TicketId ticketId) {
        super(ERROR_CODE, "Ticket with ID " + ticketId.value() + " is already reserved or unavailable");
        this.ticketId = ticketId;
    }

    public TicketId getTicketId() {
        return ticketId;
    }
}

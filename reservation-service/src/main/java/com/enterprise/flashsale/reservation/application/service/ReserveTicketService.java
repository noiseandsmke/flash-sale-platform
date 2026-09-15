package com.enterprise.flashsale.reservation.application.service;

import com.enterprise.flashsale.reservation.application.port.in.ReserveTicketCommand;
import com.enterprise.flashsale.reservation.application.port.in.ReserveTicketUseCase;
import com.enterprise.flashsale.reservation.application.port.out.TicketEventPublisherPort;
import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.domain.event.TicketReservedEvent;
import com.enterprise.flashsale.reservation.domain.model.Money;
import com.enterprise.flashsale.reservation.domain.model.Ticket;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
import com.enterprise.flashsale.reservation.domain.model.TicketStatus;

import java.time.Duration;
import java.util.Objects;

public class ReserveTicketService implements ReserveTicketUseCase {
    private static final long RESERVATION_TTL_SECONDS = 600L;
    private static final Duration RESERVATION_DURATION = Duration.ofSeconds(RESERVATION_TTL_SECONDS);

    private final TicketInventoryPort ticketInventoryPort;
    private final TicketEventPublisherPort ticketEventPublisherPort;

    public ReserveTicketService(
            TicketInventoryPort ticketInventoryPort, TicketEventPublisherPort ticketEventPublisherPort) {
        this.ticketInventoryPort = Objects.requireNonNull(ticketInventoryPort, "TicketInventoryPort must not be null");
        this.ticketEventPublisherPort =
                Objects.requireNonNull(ticketEventPublisherPort, "TicketEventPublisherPort must not be null");
    }

    @Override
    public boolean execute(ReserveTicketCommand command) {
        TicketId ticketId = TicketId.of(command.ticketId());

        boolean isStockReserved = ticketInventoryPort.reserveStock(
                command.eventId(), ticketId, command.userId(), RESERVATION_TTL_SECONDS);

        if (!isStockReserved) {
            return false;
        }

        Ticket ticket = new Ticket(
                ticketId, command.eventId(), "SEAT-" + ticketId.value(), Money.of(0.0, "USD"), TicketStatus.AVAILABLE);

        TicketReservedEvent reservationEvent = ticket.reserve(command.userId(), RESERVATION_DURATION);

        ticketEventPublisherPort.publish(reservationEvent);

        return true;
    }
}

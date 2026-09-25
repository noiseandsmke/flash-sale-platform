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
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Duration;
import java.util.Objects;

public class ReserveTicketService implements ReserveTicketUseCase {
    private static final long RESERVATION_TTL_SECONDS = 600L;
    private static final Duration RESERVATION_DURATION = Duration.ofSeconds(RESERVATION_TTL_SECONDS);

    private final TicketInventoryPort ticketInventoryPort;
    private final TicketEventPublisherPort ticketEventPublisherPort;
    private final MeterRegistry meterRegistry;

    public ReserveTicketService(
            TicketInventoryPort ticketInventoryPort,
            TicketEventPublisherPort ticketEventPublisherPort,
            MeterRegistry meterRegistry) {
        this.ticketInventoryPort = Objects.requireNonNull(ticketInventoryPort, "TicketInventoryPort must not be null");
        this.ticketEventPublisherPort =
                Objects.requireNonNull(ticketEventPublisherPort, "TicketEventPublisherPort must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean execute(ReserveTicketCommand command) {
        TicketId ticketId = TicketId.of(command.ticketId());

        boolean isStockReserved = ticketInventoryPort.reserveStock(
                command.eventId(), ticketId, command.userId(), RESERVATION_TTL_SECONDS);

        if (!isStockReserved) {
            if (meterRegistry != null) {
                meterRegistry.counter("flashsale.reservations.total", "status", "SOLD_OUT").increment();
            }
            return false;
        }

        Ticket ticket = new Ticket(
                ticketId, command.eventId(), "SEAT-" + ticketId.value(), Money.of(0.0, "USD"), TicketStatus.AVAILABLE);

        TicketReservedEvent reservationEvent = ticket.reserve(command.userId(), RESERVATION_DURATION);

        try {
            ticketEventPublisherPort.publish(reservationEvent);
            if (meterRegistry != null) {
                meterRegistry.counter("flashsale.reservations.total", "status", "SUCCESS").increment();
            }
        } catch (Exception e) {
            ticketInventoryPort.releaseStock(ticketId, command.userId());
            if (meterRegistry != null) {
                meterRegistry.counter("flashsale.reservations.total", "status", "FAILED").increment();
            }
            throw new IllegalStateException("Failed to publish reservation event; stock reservation rolled back", e);
        }

        return true;
    }
}

package com.enterprise.flashsale.reservation.domain.model;

import com.enterprise.flashsale.reservation.domain.event.TicketReservedEvent;
import com.enterprise.flashsale.reservation.domain.exception.TicketNotAvailableException;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Ticket {
    private final TicketId id;
    private final Long eventId;
    private final String seatNumber;
    private final Money price;
    private TicketStatus status;
    private String reservedByUserId;
    private Instant reservationExpiresAt;

    public Ticket(TicketId id, Long eventId, String seatNumber, Money price, TicketStatus status) {
        this.id = Objects.requireNonNull(id, "Ticket ID must not be null");
        this.eventId = Objects.requireNonNull(eventId, "Event ID must not be null");
        this.seatNumber = Objects.requireNonNull(seatNumber, "Seat number must not be null");
        this.price = Objects.requireNonNull(price, "Price must not be null");
        this.status = Objects.requireNonNull(status, "Status must not be null");
    }

    public TicketReservedEvent reserve(String userId, Duration reservationDuration) {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(reservationDuration, "Reservation duration must not be null");

        if (this.status != TicketStatus.AVAILABLE) {
            throw new TicketNotAvailableException(this.id);
        }

        this.status = this.status.transitionTo(TicketStatus.RESERVED);
        this.reservedByUserId = userId;
        this.reservationExpiresAt = Instant.now().plus(reservationDuration);

        return TicketReservedEvent.create(this.id, this.eventId, this.reservedByUserId, this.reservationExpiresAt);
    }

    public TicketId getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public Money getPrice() {
        return price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getReservedByUserId() {
        return reservedByUserId;
    }

    public Instant getReservationExpiresAt() {
        return reservationExpiresAt;
    }
}

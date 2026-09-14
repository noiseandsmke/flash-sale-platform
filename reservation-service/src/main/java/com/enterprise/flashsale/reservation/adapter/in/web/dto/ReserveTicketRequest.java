package com.enterprise.flashsale.reservation.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveTicketRequest(
        @NotNull(message = "Event ID is required") @Positive(message = "Event ID must be positive")
        Long eventId,

        @NotNull(message = "Ticket ID is required") @Positive(message = "Ticket ID must be positive")
        Long ticketId,

        @NotBlank(message = "User ID must not be blank") String userId) {}

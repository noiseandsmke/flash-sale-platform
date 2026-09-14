package com.enterprise.flashsale.reservation.adapter.in.web;

import com.enterprise.flashsale.reservation.adapter.in.web.dto.ApiResponse;
import com.enterprise.flashsale.reservation.adapter.in.web.dto.ReserveTicketRequest;
import com.enterprise.flashsale.reservation.adapter.in.web.dto.ReserveTicketResponse;
import com.enterprise.flashsale.reservation.application.port.in.ReserveTicketCommand;
import com.enterprise.flashsale.reservation.application.port.in.ReserveTicketUseCase;
import com.enterprise.flashsale.reservation.domain.exception.TicketNotAvailableException;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReserveTicketController {
    private final ReserveTicketUseCase reserveTicketUseCase;

    public ReserveTicketController(ReserveTicketUseCase reserveTicketUseCase) {
        this.reserveTicketUseCase = reserveTicketUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReserveTicketResponse>> reserve(
            @Valid @RequestBody ReserveTicketRequest request) {
        ReserveTicketCommand command = ReserveTicketCommand.of(request.eventId(), request.ticketId(), request.userId());
        boolean isReserved = reserveTicketUseCase.execute(command);

        if (!isReserved) {
            throw new TicketNotAvailableException(TicketId.of(request.ticketId()));
        }

        Instant expirationTime = Instant.now().plusSeconds(600);
        ReserveTicketResponse response = ReserveTicketResponse.of(request.ticketId(), request.userId(), expirationTime);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(response, "Ticket reserved successfully"));
    }
}

package com.enterprise.flashsale.reservation.application.port.in;

public interface ReserveTicketUseCase {
    boolean execute(ReserveTicketCommand command);
}

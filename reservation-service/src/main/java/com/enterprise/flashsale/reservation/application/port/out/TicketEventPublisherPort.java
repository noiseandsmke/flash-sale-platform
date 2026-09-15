package com.enterprise.flashsale.reservation.application.port.out;

import com.enterprise.flashsale.reservation.domain.event.TicketReservedEvent;

public interface TicketEventPublisherPort {
    void publish(TicketReservedEvent event);
}

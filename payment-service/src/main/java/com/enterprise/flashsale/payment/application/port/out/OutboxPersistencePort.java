package com.enterprise.flashsale.payment.application.port.out;

import com.enterprise.flashsale.payment.domain.event.DomainEvent;

public interface OutboxPersistencePort {
    void saveOutboxEvent(DomainEvent event);
}

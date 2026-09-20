package com.enterprise.flashsale.payment.application.port.out;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.domain.event.DomainEvent;

import java.util.List;

public interface OutboxPersistencePort {
    void saveOutboxEvent(DomainEvent event);

    List<OutboxJpaEntity> fetchPendingEvents(int batchSize);

    void markAsProcessed(OutboxJpaEntity event);
}

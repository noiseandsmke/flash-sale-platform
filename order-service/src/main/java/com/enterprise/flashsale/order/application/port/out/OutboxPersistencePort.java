package com.enterprise.flashsale.order.application.port.out;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.order.domain.event.DomainEvent;
import java.util.List;

public interface OutboxPersistencePort {
    void saveOutboxEvent(DomainEvent event);

    List<OutboxJpaEntity> fetchPendingEvents(int batchSize);

    void markAsProcessed(OutboxJpaEntity event);
}

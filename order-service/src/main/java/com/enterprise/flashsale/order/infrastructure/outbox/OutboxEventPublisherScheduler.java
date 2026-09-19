package com.enterprise.flashsale.order.infrastructure.outbox;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxEventPublisherScheduler {
    private final OutboxPersistencePort outboxPersistencePort;
    private final OrderEventPublisherPort orderEventPublisherPort;

    public OutboxEventPublisherScheduler(
            OutboxPersistencePort outboxPersistencePort, OrderEventPublisherPort orderEventPublisherPort) {
        this.outboxPersistencePort = outboxPersistencePort;
        this.orderEventPublisherPort = orderEventPublisherPort;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxJpaEntity> pendingEvents = outboxPersistencePort.fetchPendingEvents(50);
        for (OutboxJpaEntity event : pendingEvents) {
            orderEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
        }
    }
}

package com.enterprise.flashsale.order.infrastructure.outbox;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    public void processOutboxEvents() {
        for (OutboxJpaEntity event : outboxPersistencePort.fetchPendingEvents(50)) {
            orderEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
        }
    }
}

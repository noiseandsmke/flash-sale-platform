package com.enterprise.flashsale.order.adapter.in.scheduler;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OutboxEventPublisherJob {
    private final OutboxPersistencePort outboxPersistencePort;
    private final OrderEventPublisherPort orderEventPublisherPort;

    public OutboxEventPublisherJob(
            OutboxPersistencePort outboxPersistencePort, OrderEventPublisherPort orderEventPublisherPort) {
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.orderEventPublisherPort =
                Objects.requireNonNull(orderEventPublisherPort, "OrderEventPublisherPort must not be null");
    }

    @Scheduled(fixedDelay = 500)
    public void publishPendingEvents() {
        List<OutboxJpaEntity> pendingEvents = outboxPersistencePort.fetchPendingEvents(50);
        for (OutboxJpaEntity event : pendingEvents) {
            orderEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
        }
    }
}

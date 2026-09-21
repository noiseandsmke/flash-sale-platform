package com.enterprise.flashsale.payment.adapter.in.scheduler;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.PaymentEventPublisherPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OutboxEventPublisherJob {
    private final OutboxPersistencePort outboxPersistencePort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;

    public OutboxEventPublisherJob(
            OutboxPersistencePort outboxPersistencePort, PaymentEventPublisherPort paymentEventPublisherPort) {
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.paymentEventPublisherPort =
                Objects.requireNonNull(paymentEventPublisherPort, "PaymentEventPublisherPort must not be null");
    }

    @Scheduled(fixedDelay = 500)
    public void publishPendingEvents() {
        List<OutboxJpaEntity> pendingEvents = outboxPersistencePort.fetchPendingEvents(50);
        for (OutboxJpaEntity event : pendingEvents) {
            paymentEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
        }
    }
}

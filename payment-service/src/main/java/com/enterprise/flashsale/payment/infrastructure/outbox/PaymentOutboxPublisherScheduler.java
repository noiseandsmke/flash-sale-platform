package com.enterprise.flashsale.payment.infrastructure.outbox;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.PaymentEventPublisherPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentOutboxPublisherScheduler {
    private final OutboxPersistencePort outboxPersistencePort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;

    public PaymentOutboxPublisherScheduler(
            OutboxPersistencePort outboxPersistencePort, PaymentEventPublisherPort paymentEventPublisherPort) {
        this.outboxPersistencePort = outboxPersistencePort;
        this.paymentEventPublisherPort = paymentEventPublisherPort;
    }

    @Scheduled(fixedDelay = 500)
    public void processOutboxEvents() {
        for (OutboxJpaEntity event : outboxPersistencePort.fetchPendingEvents(50)) {
            paymentEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
        }
    }
}

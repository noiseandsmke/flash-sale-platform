package com.enterprise.flashsale.payment.adapter.in.scheduler;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.PaymentEventPublisherPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OutboxEventPublisherJob {
    private final OutboxPersistencePort outboxPersistencePort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;
    private final AtomicInteger pendingGauge;
    private final Timer outboxLagTimer;

    public OutboxEventPublisherJob(
            OutboxPersistencePort outboxPersistencePort,
            PaymentEventPublisherPort paymentEventPublisherPort,
            MeterRegistry meterRegistry) {
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.paymentEventPublisherPort =
                Objects.requireNonNull(paymentEventPublisherPort, "PaymentEventPublisherPort must not be null");
        this.pendingGauge = meterRegistry.gauge("flashsale.outbox.pending", new AtomicInteger(0));
        this.outboxLagTimer = Timer.builder("flashsale.outbox.event.lag")
                .description("Lag from event creation to publication")
                .publishPercentiles(0.95, 0.99)
                .register(meterRegistry);
    }

    @Scheduled(fixedDelay = 500)
    public void publishPendingEvents() {
        List<OutboxJpaEntity> pendingEvents = outboxPersistencePort.fetchPendingEvents(50);
        pendingGauge.set(pendingEvents.size());
        for (OutboxJpaEntity event : pendingEvents) {
            paymentEventPublisherPort.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            outboxPersistencePort.markAsProcessed(event);
            if (event.getCreatedAt() != null) {
                long lagMs = Math.max(0, System.currentTimeMillis() - event.getCreatedAt().toEpochMilli());
                outboxLagTimer.record(lagMs, TimeUnit.MILLISECONDS);
            }
        }
    }
}

package com.enterprise.flashsale.order.adapter.out.persistence;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.ProcessedEventJpaEntity;
import com.enterprise.flashsale.order.adapter.out.persistence.repository.SpringDataProcessedEventRepository;
import com.enterprise.flashsale.order.application.port.out.ProcessedEventCheckPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PostgresProcessedEventAdapter implements ProcessedEventCheckPort {
    private final SpringDataProcessedEventRepository repository;
    private final Counter duplicateCounter;

    public PostgresProcessedEventAdapter(
            SpringDataProcessedEventRepository repository,
            MeterRegistry meterRegistry) {
        this.repository = repository;
        this.duplicateCounter = Counter.builder("flashsale.idempotency.duplicates.total")
                .tag("service", "order-service")
                .description("Total duplicate events blocked by idempotency check")
                .register(meterRegistry);
    }

    @Override
    public boolean isProcessed(UUID eventId) {
        boolean exists = repository.existsById(eventId.toString());
        if (exists) {
            duplicateCounter.increment();
        }
        return exists;
    }

    @Override
    public void markAsProcessed(UUID eventId, String eventType) {
        ProcessedEventJpaEntity entity = new ProcessedEventJpaEntity(eventId.toString(), eventType, Instant.now());
        repository.save(entity);
    }
}

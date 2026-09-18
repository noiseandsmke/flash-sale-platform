package com.enterprise.flashsale.payment.adapter.out.persistence;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.ProcessedEventJpaEntity;
import com.enterprise.flashsale.payment.adapter.out.persistence.repository.SpringDataProcessedEventRepository;
import com.enterprise.flashsale.payment.application.port.out.ProcessedEventCheckPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PostgresProcessedEventAdapter implements ProcessedEventCheckPort {
    private final SpringDataProcessedEventRepository repository;

    public PostgresProcessedEventAdapter(SpringDataProcessedEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isProcessed(UUID eventId) {
        return repository.existsById(eventId.toString());
    }

    @Override
    public void markAsProcessed(UUID eventId, String eventType) {
        ProcessedEventJpaEntity entity = new ProcessedEventJpaEntity(eventId.toString(), eventType, Instant.now());
        repository.save(entity);
    }
}

package com.enterprise.flashsale.order.adapter.out.persistence;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.order.adapter.out.persistence.repository.SpringDataOutboxRepository;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.order.domain.event.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class PostgresOutboxAdapter implements OutboxPersistencePort {
    private final SpringDataOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public PostgresOutboxAdapter(SpringDataOutboxRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveOutboxEvent(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxJpaEntity entity = new OutboxJpaEntity(
                    UUID.randomUUID().toString(),
                    "Order",
                    event.aggregateId(),
                    event.eventType(),
                    payload,
                    "PENDING",
                    0,
                    Instant.now());
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize domain event for outbox", e);
        }
    }

    @Override
    public List<OutboxJpaEntity> fetchPendingEvents(int batchSize) {
        return repository.findPendingEvents(PageRequest.of(0, batchSize));
    }

    @Override
    public void markAsProcessed(OutboxJpaEntity event) {
        event.setStatus("PROCESSED");
        event.setProcessedAt(Instant.now());
        repository.save(event);
    }
}

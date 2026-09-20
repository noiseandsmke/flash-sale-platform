package com.enterprise.flashsale.payment.adapter.out.persistence;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.adapter.out.persistence.repository.SpringDataOutboxRepository;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.domain.event.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
                    event.eventId().toString(),
                    "Payment",
                    event.aggregateId(),
                    event.eventType(),
                    payload,
                    "PENDING",
                    0,
                    event.occurredOn());
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize domain event to JSON", e);
        }
    }

    @Override
    public List<OutboxJpaEntity> fetchPendingEvents(int batchSize) {
        return repository.findPendingEvents(PageRequest.of(0, batchSize));
    }

    @Override
    @Transactional
    public void markAsProcessed(OutboxJpaEntity event) {
        event.setStatus("PROCESSED");
        event.setProcessedAt(Instant.now());
        repository.save(event);
    }
}

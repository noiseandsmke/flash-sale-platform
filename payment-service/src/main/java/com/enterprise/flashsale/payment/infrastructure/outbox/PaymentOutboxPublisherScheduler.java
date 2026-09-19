package com.enterprise.flashsale.payment.infrastructure.outbox;

import com.enterprise.flashsale.payment.adapter.out.messaging.KafkaPaymentEventRelayAdapter;
import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import com.enterprise.flashsale.payment.adapter.out.persistence.repository.SpringDataOutboxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class PaymentOutboxPublisherScheduler {
    private final SpringDataOutboxRepository outboxRepository;
    private final KafkaPaymentEventRelayAdapter kafkaPaymentRelayAdapter;

    public PaymentOutboxPublisherScheduler(
            SpringDataOutboxRepository outboxRepository, KafkaPaymentEventRelayAdapter kafkaPaymentRelayAdapter) {
        this.outboxRepository = outboxRepository;
        this.kafkaPaymentRelayAdapter = kafkaPaymentRelayAdapter;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxJpaEntity> pendingEvents = outboxRepository.findPendingEvents(PageRequest.of(0, 50));

        for (OutboxJpaEntity event : pendingEvents) {
            kafkaPaymentRelayAdapter.publish(event.getAggregateId(), event.getEventType(), event.getPayload());
            event.setStatus("PROCESSED");
            event.setProcessedAt(Instant.now());
            outboxRepository.save(event);
        }
    }
}

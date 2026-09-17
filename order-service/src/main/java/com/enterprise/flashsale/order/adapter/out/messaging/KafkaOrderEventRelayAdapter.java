package com.enterprise.flashsale.order.adapter.out.messaging;

import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaOrderEventRelayAdapter implements OrderEventPublisherPort {
    private static final String TOPIC_ORDER_CREATED = "order-created-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaOrderEventRelayAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String aggregateId, String payload) {
        try {
            kafkaTemplate.send(TOPIC_ORDER_CREATED, aggregateId, payload).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish event to Kafka within timeout", e);
        }
    }
}

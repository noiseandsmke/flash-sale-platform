package com.enterprise.flashsale.order.adapter.out.messaging;

import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaOrderEventRelayAdapter implements OrderEventPublisherPort {
    private static final String TOPIC_ORDER_CREATED = "order-created-events";
    private static final String TOPIC_ORDER_CANCELLED = "order-cancelled-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaOrderEventRelayAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String aggregateId, String eventType, String payload) {
        try {
            String targetTopic = "ORDER_CANCELLED".equals(eventType) ? TOPIC_ORDER_CANCELLED : TOPIC_ORDER_CREATED;
            ProducerRecord<String, String> record = new ProducerRecord<>(targetTopic, aggregateId, payload);
            if (eventType != null) {
                record.headers().add("eventType", eventType.getBytes(StandardCharsets.UTF_8));
            }
            kafkaTemplate.send(record).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish event to Kafka within timeout", e);
        }
    }
}

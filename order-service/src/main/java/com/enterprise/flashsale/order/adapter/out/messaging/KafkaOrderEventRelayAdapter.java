package com.enterprise.flashsale.order.adapter.out.messaging;

import com.enterprise.flashsale.order.application.port.out.OrderEventPublisherPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaOrderEventRelayAdapter implements OrderEventPublisherPort {
    private static final String TOPIC_ORDER_CREATED = "order-created-events";
    private static final String TOPIC_ORDER_CANCELLED = "order-cancelled-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventRelayAdapter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String aggregateId, String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            boolean isCancelled = root != null && (root.has("ticketIds") || root.has("reason") || "ORDER_CANCELLED".equals(root.path("eventType").asText()));
            String targetTopic = isCancelled ? TOPIC_ORDER_CANCELLED : TOPIC_ORDER_CREATED;

            kafkaTemplate.send(targetTopic, aggregateId, payload).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish event to Kafka within timeout", e);
        }
    }
}

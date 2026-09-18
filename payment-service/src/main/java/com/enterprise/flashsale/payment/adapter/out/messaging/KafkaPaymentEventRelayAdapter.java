package com.enterprise.flashsale.payment.adapter.out.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaPaymentEventRelayAdapter {
    private static final String TOPIC_PAYMENT_EVENTS = "payment-events";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPaymentEventRelayAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String aggregateId, String payload) {
        try {
            kafkaTemplate.send(TOPIC_PAYMENT_EVENTS, aggregateId, payload).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish payment event to Kafka within timeout", e);
        }
    }
}

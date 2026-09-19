package com.enterprise.flashsale.payment.adapter.out.messaging;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaPaymentEventRelayAdapter {
    private static final String TOPIC_PAYMENT_EVENTS = "payment-events";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPaymentEventRelayAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String aggregateId, String eventType, String payload) {
        try {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_PAYMENT_EVENTS, aggregateId, payload);
            if (eventType != null) {
                record.headers().add("eventType", eventType.getBytes(StandardCharsets.UTF_8));
            }
            kafkaTemplate.send(record).get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish payment event to Kafka within timeout", e);
        }
    }
}

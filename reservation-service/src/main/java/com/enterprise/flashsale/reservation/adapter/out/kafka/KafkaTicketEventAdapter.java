package com.enterprise.flashsale.reservation.adapter.out.kafka;

import com.enterprise.flashsale.reservation.application.port.out.TicketEventPublisherPort;
import com.enterprise.flashsale.reservation.domain.event.TicketReservedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTicketEventAdapter implements TicketEventPublisherPort {
    private static final String TOPIC_TICKET_RESERVED = "ticket-reserved-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaTicketEventAdapter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TicketReservedEvent event) {
        try {
            String partitionKey = event.aggregateId();
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_TICKET_RESERVED, partitionKey, payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize TicketReservedEvent to JSON", e);
        }
    }
}

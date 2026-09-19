package com.enterprise.flashsale.reservation.adapter.in.kafka;

import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelledConsumer.class);
    private final TicketInventoryPort ticketInventoryPort;
    private final ObjectMapper objectMapper;

    public OrderCancelledConsumer(TicketInventoryPort ticketInventoryPort, ObjectMapper objectMapper) {
        this.ticketInventoryPort = ticketInventoryPort;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "order-cancelled-events",
            groupId = "reservation-cancellation-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String payload, Acknowledgment acknowledgment) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || !root.has("userId")) {
                log.error("Received malformed order cancelled event payload: {}", payload);
                acknowledgment.acknowledge();
                return;
            }
            String userId = root.get("userId").asText();
            JsonNode ticketIdsNode = root.get("ticketIds");

            if (ticketIdsNode != null && ticketIdsNode.isArray()) {
                for (JsonNode idNode : ticketIdsNode) {
                    TicketId ticketId = TicketId.of(idNode.asLong());
                    boolean released = ticketInventoryPort.releaseStock(ticketId, userId);
                    log.info(
                            "Compensating Transaction: Released Redis lock for ticketId={}, userId={}, result={}",
                            ticketId.value(),
                            userId,
                            released);
                }
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to process order cancellation in reservation-service", e);
        }
    }
}

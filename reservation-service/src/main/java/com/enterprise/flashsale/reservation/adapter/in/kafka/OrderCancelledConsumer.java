package com.enterprise.flashsale.reservation.adapter.in.kafka;

import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OrderCancelledConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelledConsumer.class);
    private final TicketInventoryPort ticketInventoryPort;
    private final ObjectMapper objectMapper;
    private final Timer transitLagTimer;

    public OrderCancelledConsumer(
            TicketInventoryPort ticketInventoryPort, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.ticketInventoryPort = ticketInventoryPort;
        this.objectMapper = objectMapper;
        this.transitLagTimer = Timer.builder("flashsale.kafka.transit.lag")
                .tag("topic", "order-cancelled-events")
                .description("Transit lag between Kafka record publish timestamp and consumer reception")
                .publishPercentiles(0.95, 0.99)
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "order-cancelled-events",
            groupId = "reservation-cancellation-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        long lagMs = Math.max(0, System.currentTimeMillis() - record.timestamp());
        transitLagTimer.record(lagMs, TimeUnit.MILLISECONDS);

        try {
            JsonNode root = objectMapper.readTree(record.value());
            if (root == null || !root.has("userId")) {
                log.error("Received malformed order cancelled event payload: {}", record.value());
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

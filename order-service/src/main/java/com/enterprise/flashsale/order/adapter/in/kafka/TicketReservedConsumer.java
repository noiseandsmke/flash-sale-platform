package com.enterprise.flashsale.order.adapter.in.kafka;

import com.enterprise.flashsale.order.adapter.in.kafka.dto.TicketReservedMessage;
import com.enterprise.flashsale.order.application.port.in.CreateOrderCommand;
import com.enterprise.flashsale.order.application.port.in.CreateOrderUseCase;
import com.enterprise.flashsale.order.domain.model.Money;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

@Component
public class TicketReservedConsumer {
    private final CreateOrderUseCase createOrderUseCase;
    private final ObjectMapper objectMapper;
    private final Timer transitLagTimer;

    public TicketReservedConsumer(
            CreateOrderUseCase createOrderUseCase,
            ObjectMapper objectMapper,
            MeterRegistry meterRegistry) {
        this.createOrderUseCase = createOrderUseCase;
        this.objectMapper = objectMapper;
        this.transitLagTimer = Timer.builder("flashsale.kafka.transit.lag")
                .tag("topic", "ticket-reserved-events")
                .description("Transit lag between Kafka record publish timestamp and consumer reception")
                .publishPercentiles(0.95, 0.99)
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "ticket-reserved-events",
            groupId = "order-fulfillment-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        long lagMs = Math.max(0, System.currentTimeMillis() - record.timestamp());
        transitLagTimer.record(lagMs, TimeUnit.MILLISECONDS);

        try {
            TicketReservedMessage message = objectMapper.readValue(record.value(), TicketReservedMessage.class);

            CreateOrderCommand command = new CreateOrderCommand(
                    message.eventId(),
                    Long.parseLong(message.aggregateId()),
                    message.eventIdRef(),
                    message.userId(),
                    new Money(BigDecimal.valueOf(50.00), Currency.getInstance("USD")));

            createOrderUseCase.execute(command);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Corrupted message payload", e);
        }
    }
}

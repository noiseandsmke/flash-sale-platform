package com.enterprise.flashsale.order.adapter.in.kafka;

import com.enterprise.flashsale.order.adapter.in.kafka.dto.TicketReservedMessage;
import com.enterprise.flashsale.order.application.port.in.CreateOrderCommand;
import com.enterprise.flashsale.order.application.port.in.CreateOrderUseCase;
import com.enterprise.flashsale.order.domain.model.Money;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;

@Component
public class TicketReservedConsumer {
    private final CreateOrderUseCase createOrderUseCase;
    private final ObjectMapper objectMapper;

    public TicketReservedConsumer(CreateOrderUseCase createOrderUseCase, ObjectMapper objectMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @KafkaListener(
            topics = "ticket-reserved-events",
            groupId = "order-fulfillment-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String messagePayload, Acknowledgment acknowledgment) {
        try {
            TicketReservedMessage message = objectMapper.readValue(messagePayload, TicketReservedMessage.class);

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

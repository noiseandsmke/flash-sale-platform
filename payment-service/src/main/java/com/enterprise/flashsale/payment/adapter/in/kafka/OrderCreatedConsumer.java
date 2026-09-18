package com.enterprise.flashsale.payment.adapter.in.kafka;

import com.enterprise.flashsale.payment.adapter.in.kafka.dto.OrderCreatedMessage;
import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentCommand;
import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentUseCase;
import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class OrderCreatedConsumer {
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ObjectMapper objectMapper;

    public OrderCreatedConsumer(ProcessPaymentUseCase processPaymentUseCase, ObjectMapper objectMapper) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "order-created-events",
            groupId = "payment-processing-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String messagePayload, Acknowledgment acknowledgment) {
        try {
            OrderCreatedMessage message = objectMapper.readValue(messagePayload, OrderCreatedMessage.class);

            Money amount = Money.of(
                    message.totalAmount().amount(),
                    Currency.getInstance(message.totalAmount().currency()));

            ProcessPaymentCommand command = new ProcessPaymentCommand(
                    message.eventId(), message.aggregateId(), message.userId(), amount, PaymentMethod.CREDIT_CARD);

            processPaymentUseCase.execute(command);

            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Corrupted order created event payload", e);
        }
    }
}

package com.enterprise.flashsale.payment.adapter.in.kafka;

import com.enterprise.flashsale.payment.adapter.in.kafka.dto.OrderCreatedMessage;
import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentCommand;
import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentUseCase;
import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.concurrent.TimeUnit;

@Component
public class OrderCreatedConsumer {
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ObjectMapper objectMapper;
    private final Timer transitLagTimer;

    public OrderCreatedConsumer(
            ProcessPaymentUseCase processPaymentUseCase, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.objectMapper = objectMapper;
        this.transitLagTimer = Timer.builder("flashsale.kafka.transit.lag")
                .tag("topic", "order-created-events")
                .description("Transit lag between Kafka record publish timestamp and consumer reception")
                .publishPercentiles(0.95, 0.99)
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "order-created-events",
            groupId = "payment-processing-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        long lagMs = Math.max(0, System.currentTimeMillis() - record.timestamp());
        transitLagTimer.record(lagMs, TimeUnit.MILLISECONDS);

        try {
            OrderCreatedMessage message = objectMapper.readValue(record.value(), OrderCreatedMessage.class);

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

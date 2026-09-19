package com.enterprise.flashsale.order.adapter.in.kafka;

import com.enterprise.flashsale.order.application.port.in.CancelOrderUseCase;
import com.enterprise.flashsale.order.application.port.in.CompleteOrderUseCase;
import com.enterprise.flashsale.order.domain.exception.InvalidOrderStateException;
import com.enterprise.flashsale.order.domain.model.OrderId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final CompleteOrderUseCase completeOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(
            CompleteOrderUseCase completeOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            ObjectMapper objectMapper) {
        this.completeOrderUseCase = completeOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "order-payment-feedback-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String payload, Acknowledgment acknowledgment) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || !root.has("orderId")) {
                log.error("Received malformed payment event payload (missing orderId): {}", payload);
                acknowledgment.acknowledge();
                return;
            }

            String orderIdStr = root.get("orderId").asText();
            OrderId orderId = OrderId.fromString(orderIdStr);

            boolean isFailure = root.has("failureReason")
                    || "PAYMENT_FAILED".equals(root.path("eventType").asText());

            try {
                if (isFailure) {
                    String reason = root.has("failureReason")
                            ? root.get("failureReason").asText()
                            : "PAYMENT_FAILED";
                    cancelOrderUseCase.cancelOrder(orderId, reason);
                } else {
                    completeOrderUseCase.completeOrder(orderId);
                }
            } catch (InvalidOrderStateException e) {
                log.warn(
                        "Order {} is already in target state. Skipping duplicate event. Reason: {}",
                        orderIdStr,
                        e.getMessage());
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to process payment event payload: " + payload, e);
        }
    }
}

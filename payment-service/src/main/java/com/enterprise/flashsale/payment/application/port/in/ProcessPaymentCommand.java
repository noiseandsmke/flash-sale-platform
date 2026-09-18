package com.enterprise.flashsale.payment.application.port.in;

import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentMethod;

import java.util.Objects;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID eventId, String orderId, String userId, Money amount, PaymentMethod paymentMethod) {
    public ProcessPaymentCommand {
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(orderId, "Order ID must not be null");
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(paymentMethod, "Payment method must not be null");

        if (orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID must not be blank");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }
    }
}

package com.enterprise.flashsale.payment.domain.model;

import com.enterprise.flashsale.payment.domain.event.PaymentFailedEvent;
import com.enterprise.flashsale.payment.domain.event.PaymentProcessedEvent;

import java.time.Instant;
import java.util.Objects;

public class Payment {
    private final PaymentId id;
    private final String orderId;
    private final String userId;
    private final Money amount;
    private final PaymentMethod paymentMethod;
    private final Instant createdAt;
    private PaymentStatus status;
    private String transactionReference;
    private Instant updatedAt;

    public Payment(
            PaymentId id,
            String orderId,
            String userId,
            Money amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            String transactionReference,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Payment ID must not be null");
        this.orderId = Objects.requireNonNull(orderId, "Order ID must not be null");
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        this.status = Objects.requireNonNull(status, "Payment status must not be null");
        this.transactionReference = transactionReference;
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");
    }

    public static Payment create(
            PaymentId id, String orderId, String userId, Money amount, PaymentMethod paymentMethod) {
        Instant now = Instant.now();
        return new Payment(id, orderId, userId, amount, paymentMethod, PaymentStatus.PENDING, null, now, now);
    }

    public PaymentProcessedEvent markAsSuccess(String transactionReference) {
        this.status = this.status.transitionTo(PaymentStatus.SUCCESS);
        this.transactionReference = Objects.requireNonNull(
                transactionReference, "Transaction reference must not be null for successful payment");
        this.updatedAt = Instant.now();
        return PaymentProcessedEvent.from(this.id, this.orderId, this.userId, this.amount, this.transactionReference);
    }

    public PaymentFailedEvent markAsFailed(String failureReason) {
        this.status = this.status.transitionTo(PaymentStatus.FAILED);
        this.updatedAt = Instant.now();
        return PaymentFailedEvent.from(this.id, this.orderId, this.userId, this.amount, failureReason);
    }

    public PaymentId getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

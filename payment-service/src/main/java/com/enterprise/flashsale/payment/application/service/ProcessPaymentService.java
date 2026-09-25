package com.enterprise.flashsale.payment.application.service;

import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentCommand;
import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentUseCase;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.PaymentGatewayPort;
import com.enterprise.flashsale.payment.application.port.out.PaymentGatewayPort.GatewayExecutionResult;
import com.enterprise.flashsale.payment.application.port.out.PaymentPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.ProcessedEventCheckPort;
import com.enterprise.flashsale.payment.domain.event.DomainEvent;
import com.enterprise.flashsale.payment.domain.model.Payment;
import com.enterprise.flashsale.payment.domain.model.PaymentId;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Objects;

public class ProcessPaymentService implements ProcessPaymentUseCase {
    private final PaymentPersistencePort paymentPersistencePort;
    private final OutboxPersistencePort outboxPersistencePort;
    private final ProcessedEventCheckPort processedEventCheckPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final MeterRegistry meterRegistry;

    public ProcessPaymentService(
            PaymentPersistencePort paymentPersistencePort,
            OutboxPersistencePort outboxPersistencePort,
            ProcessedEventCheckPort processedEventCheckPort,
            PaymentGatewayPort paymentGatewayPort,
            MeterRegistry meterRegistry) {
        this.paymentPersistencePort =
                Objects.requireNonNull(paymentPersistencePort, "PaymentPersistencePort must not be null");
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.processedEventCheckPort =
                Objects.requireNonNull(processedEventCheckPort, "ProcessedEventCheckPort must not be null");
        this.paymentGatewayPort = Objects.requireNonNull(paymentGatewayPort, "PaymentGatewayPort must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public PaymentId execute(ProcessPaymentCommand command) {
        if (processedEventCheckPort.isProcessed(command.eventId())) {
            return null;
        }

        PaymentId paymentId = PaymentId.generate();

        Payment payment = Payment.create(
                paymentId, command.orderId(), command.userId(), command.amount(), command.paymentMethod());

        GatewayExecutionResult gatewayResult = paymentGatewayPort.charge(
                command.orderId(), command.userId(), command.amount(), command.paymentMethod());

        DomainEvent event;
        if (gatewayResult.isSuccessful()) {
            event = payment.markAsSuccess(gatewayResult.transactionReference());
            if (meterRegistry != null) {
                meterRegistry.counter("flashsale.payments.total", "status", "SUCCESS", "gateway", "stripe").increment();
            }
        } else {
            event = payment.markAsFailed(gatewayResult.failureReason());
            if (meterRegistry != null) {
                meterRegistry.counter("flashsale.payments.total", "status", "FAILED", "gateway", "stripe").increment();
            }
        }

        paymentPersistencePort.save(payment);
        outboxPersistencePort.saveOutboxEvent(event);

        processedEventCheckPort.markAsProcessed(command.eventId(), event.eventType());

        return paymentId;
    }
}

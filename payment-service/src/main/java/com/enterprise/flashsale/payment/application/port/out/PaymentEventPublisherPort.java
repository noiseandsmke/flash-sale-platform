package com.enterprise.flashsale.payment.application.port.out;

public interface PaymentEventPublisherPort {
    void publish(String aggregateId, String eventType, String payload);
}

package com.enterprise.flashsale.order.application.port.out;

public interface OrderEventPublisherPort {
    void publish(String aggregateId, String payload);
}

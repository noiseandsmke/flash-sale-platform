package com.enterprise.flashsale.order.application.service;

import com.enterprise.flashsale.order.application.port.in.CancelOrderUseCase;
import com.enterprise.flashsale.order.application.port.in.CompleteOrderUseCase;
import com.enterprise.flashsale.order.application.port.out.OrderPersistencePort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.order.domain.event.OrderCancelledEvent;
import com.enterprise.flashsale.order.domain.exception.OrderNotFoundException;
import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Objects;

public class OrderStateUpdateService implements CompleteOrderUseCase, CancelOrderUseCase {
    private final OrderPersistencePort orderPersistencePort;
    private final OutboxPersistencePort outboxPersistencePort;
    private final MeterRegistry meterRegistry;

    public OrderStateUpdateService(
            OrderPersistencePort orderPersistencePort,
            OutboxPersistencePort outboxPersistencePort,
            MeterRegistry meterRegistry) {
        this.orderPersistencePort =
                Objects.requireNonNull(orderPersistencePort, "OrderPersistencePort must not be null");
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void completeOrder(OrderId orderId) {
        Order order = orderPersistencePort.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markAsPaid();
        orderPersistencePort.save(order);
    }

    @Override
    public void cancelOrder(OrderId orderId, String reason) {
        Order order = orderPersistencePort.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        OrderCancelledEvent cancelledEvent = order.cancel(reason);
        orderPersistencePort.save(order);
        outboxPersistencePort.saveOutboxEvent(cancelledEvent);
        if (meterRegistry != null) {
            meterRegistry.counter("flashsale.orders.total", "status", "CANCELLED").increment();
        }
    }
}

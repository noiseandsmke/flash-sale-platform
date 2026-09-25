package com.enterprise.flashsale.order.application.service;

import com.enterprise.flashsale.order.application.port.in.CreateOrderCommand;
import com.enterprise.flashsale.order.application.port.in.CreateOrderUseCase;
import com.enterprise.flashsale.order.application.port.out.OrderPersistencePort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.order.application.port.out.ProcessedEventCheckPort;
import com.enterprise.flashsale.order.domain.event.OrderCreatedEvent;
import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;
import com.enterprise.flashsale.order.domain.model.OrderItem;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;
import java.util.Objects;

public class CreateOrderService implements CreateOrderUseCase {
    private final OrderPersistencePort orderPersistencePort;
    private final OutboxPersistencePort outboxPersistencePort;
    private final ProcessedEventCheckPort processedEventCheckPort;
    private final MeterRegistry meterRegistry;

    public CreateOrderService(
            OrderPersistencePort orderPersistencePort,
            OutboxPersistencePort outboxPersistencePort,
            ProcessedEventCheckPort processedEventCheckPort,
            MeterRegistry meterRegistry) {
        this.orderPersistencePort =
                Objects.requireNonNull(orderPersistencePort, "OrderPersistencePort must not be null");
        this.outboxPersistencePort =
                Objects.requireNonNull(outboxPersistencePort, "OutboxPersistencePort must not be null");
        this.processedEventCheckPort =
                Objects.requireNonNull(processedEventCheckPort, "ProcessedEventCheckPort must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public OrderId execute(CreateOrderCommand command) {
        if (processedEventCheckPort.isProcessed(command.eventId())) {
            return null;
        }

        OrderId orderId = OrderId.generate();
        OrderItem item = new OrderItem(command.ticketId(), command.eventIdRef(), command.price());
        Order order = Order.create(orderId, command.userId(), List.of(item));

        orderPersistencePort.save(order);

        OrderCreatedEvent event = order.toCreatedEvent();
        outboxPersistencePort.saveOutboxEvent(event);

        processedEventCheckPort.markAsProcessed(command.eventId(), event.eventType());

        if (meterRegistry != null) {
            meterRegistry.counter("flashsale.orders.total", "status", "CREATED").increment();
        }

        return orderId;
    }
}

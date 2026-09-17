package com.enterprise.flashsale.order.application.service;

import com.enterprise.flashsale.order.application.port.in.GetOrderQueryUseCase;
import com.enterprise.flashsale.order.application.port.out.OrderPersistencePort;
import com.enterprise.flashsale.order.domain.exception.OrderNotFoundException;
import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;

import java.util.Objects;

public class OrderQueryService implements GetOrderQueryUseCase {
    private final OrderPersistencePort orderPersistencePort;

    public OrderQueryService(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort =
                Objects.requireNonNull(orderPersistencePort, "OrderPersistencePort must not be null");
    }

    @Override
    public Order getOrderById(OrderId orderId) {
        return orderPersistencePort.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}

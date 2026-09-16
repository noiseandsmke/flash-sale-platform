package com.enterprise.flashsale.order.application.port.out;

import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;

import java.util.Optional;

public interface OrderPersistencePort {
    Order save(Order order);

    Optional<Order> findById(OrderId orderId);
}

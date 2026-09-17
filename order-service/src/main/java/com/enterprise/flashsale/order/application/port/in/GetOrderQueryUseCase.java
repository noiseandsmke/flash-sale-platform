package com.enterprise.flashsale.order.application.port.in;

import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;

public interface GetOrderQueryUseCase {
    Order getOrderById(OrderId orderId);
}

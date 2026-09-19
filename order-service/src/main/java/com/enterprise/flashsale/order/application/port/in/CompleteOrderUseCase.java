package com.enterprise.flashsale.order.application.port.in;

import com.enterprise.flashsale.order.domain.model.OrderId;

public interface CompleteOrderUseCase {
    void completeOrder(OrderId orderId);
}

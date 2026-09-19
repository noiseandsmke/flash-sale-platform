package com.enterprise.flashsale.order.application.port.in;

import com.enterprise.flashsale.order.domain.model.OrderId;

public interface CancelOrderUseCase {
    void cancelOrder(OrderId orderId, String reason);
}

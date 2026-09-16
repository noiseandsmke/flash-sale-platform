package com.enterprise.flashsale.order.domain.exception;

import com.enterprise.flashsale.order.domain.model.OrderId;

public class OrderNotFoundException extends DomainException {
    private static final String ERROR_CODE = "ORDER_NOT_FOUND";
    private final OrderId orderId;

    public OrderNotFoundException(OrderId orderId) {
        super(ERROR_CODE, "Order not found with ID: " + orderId.value());
        this.orderId = orderId;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}

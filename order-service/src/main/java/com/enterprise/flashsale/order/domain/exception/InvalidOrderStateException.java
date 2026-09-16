package com.enterprise.flashsale.order.domain.exception;

public class InvalidOrderStateException extends DomainException {
    private static final String ERROR_CODE = "INVALID_ORDER_STATE";

    public InvalidOrderStateException(String message) {
        super(ERROR_CODE, message);
    }
}

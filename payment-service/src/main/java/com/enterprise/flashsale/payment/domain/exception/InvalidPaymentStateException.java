package com.enterprise.flashsale.payment.domain.exception;

public class InvalidPaymentStateException extends DomainException {
    private static final String ERROR_CODE = "INVALID_PAYMENT_STATE";

    public InvalidPaymentStateException(String message) {
        super(ERROR_CODE, message);
    }
}

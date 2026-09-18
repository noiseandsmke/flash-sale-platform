package com.enterprise.flashsale.payment.domain.exception;

import com.enterprise.flashsale.payment.domain.model.PaymentId;

public class PaymentNotFoundException extends DomainException {
    private static final String ERROR_CODE = "PAYMENT_NOT_FOUND";
    private final PaymentId paymentId;

    public PaymentNotFoundException(PaymentId paymentId) {
        super(ERROR_CODE, "Payment transaction not found with ID: " + paymentId.value());
        this.paymentId = paymentId;
    }

    public PaymentId getPaymentId() {
        return paymentId;
    }
}

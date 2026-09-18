package com.enterprise.flashsale.payment.application.port.in;

import com.enterprise.flashsale.payment.domain.model.PaymentId;

public interface ProcessPaymentUseCase {
    PaymentId execute(ProcessPaymentCommand command);
}

package com.enterprise.flashsale.payment.application.port.out;

import com.enterprise.flashsale.payment.domain.model.Payment;
import com.enterprise.flashsale.payment.domain.model.PaymentId;

import java.util.Optional;

public interface PaymentPersistencePort {
    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId paymentId);

    Optional<Payment> findByOrderId(String orderId);
}

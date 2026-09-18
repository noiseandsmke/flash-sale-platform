package com.enterprise.flashsale.payment.application.port.out;

import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentMethod;

public interface PaymentGatewayPort {
    GatewayExecutionResult charge(String orderId, String userId, Money amount, PaymentMethod paymentMethod);

    record GatewayExecutionResult(boolean isSuccessful, String transactionReference, String failureReason) {
        public static GatewayExecutionResult success(String transactionReference) {
            return new GatewayExecutionResult(true, transactionReference, null);
        }

        public static GatewayExecutionResult failure(String failureReason) {
            return new GatewayExecutionResult(false, null, failureReason);
        }
    }
}

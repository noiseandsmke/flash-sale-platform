package com.enterprise.flashsale.payment.adapter.out.gateway;

import com.enterprise.flashsale.payment.application.port.out.PaymentGatewayPort;
import com.enterprise.flashsale.payment.domain.model.Money;
import com.enterprise.flashsale.payment.domain.model.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGatewayAdapter implements PaymentGatewayPort {
    private static final Logger log = LoggerFactory.getLogger(MockPaymentGatewayAdapter.class);
    private static final BigDecimal FRAUD_THRESHOLD_AMOUNT = BigDecimal.valueOf(10000.00);

    @Override
    public GatewayExecutionResult charge(String orderId, String userId, Money amount, PaymentMethod paymentMethod) {
        log.info(
                "Processing external payment charge: orderId={}, userId={}, amount={} {}",
                orderId,
                userId,
                amount.amount(),
                amount.currency().getCurrencyCode());

        if (userId.startsWith("fail_") || userId.contains("insufficient")) {
            log.warn("Payment rejected by gateway: Insufficient funds for user {}", userId);
            return GatewayExecutionResult.failure("PAYMENT_REJECTED_INSUFFICIENT_FUNDS");
        }

        if (amount.amount().compareTo(FRAUD_THRESHOLD_AMOUNT) > 0) {
            log.warn("Payment rejected by gateway: Amount exceeds fraud threshold: {}", amount.amount());
            return GatewayExecutionResult.failure("TRANSACTION_LIMIT_EXCEEDED");
        }

        String transactionRef = "TXN-" + UUID.randomUUID();
        log.info("Payment charged successfully via {}. Reference: {}", paymentMethod, transactionRef);
        return GatewayExecutionResult.success(transactionRef);
    }
}

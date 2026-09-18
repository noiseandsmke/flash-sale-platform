package com.enterprise.flashsale.payment.adapter.out.persistence.mapper;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.PaymentJpaEntity;
import com.enterprise.flashsale.payment.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class PaymentPersistenceMapper {
    public PaymentJpaEntity toJpaEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId().toString(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount().amount(),
                payment.getAmount().currency().getCurrencyCode(),
                payment.getPaymentMethod().name(),
                payment.getStatus().name(),
                payment.getTransactionReference(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }

    public Payment toDomainEntity(PaymentJpaEntity entity) {
        return new Payment(
                PaymentId.fromString(entity.getId()),
                entity.getOrderId(),
                entity.getUserId(),
                Money.of(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                PaymentMethod.valueOf(entity.getPaymentMethod()),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getTransactionReference(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

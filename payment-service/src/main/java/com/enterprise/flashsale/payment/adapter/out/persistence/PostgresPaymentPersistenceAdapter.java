package com.enterprise.flashsale.payment.adapter.out.persistence;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.PaymentJpaEntity;
import com.enterprise.flashsale.payment.adapter.out.persistence.mapper.PaymentPersistenceMapper;
import com.enterprise.flashsale.payment.adapter.out.persistence.repository.SpringDataPaymentRepository;
import com.enterprise.flashsale.payment.application.port.out.PaymentPersistencePort;
import com.enterprise.flashsale.payment.domain.model.Payment;
import com.enterprise.flashsale.payment.domain.model.PaymentId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PostgresPaymentPersistenceAdapter implements PaymentPersistencePort {
    private final SpringDataPaymentRepository repository;
    private final PaymentPersistenceMapper mapper;

    public PostgresPaymentPersistenceAdapter(SpringDataPaymentRepository repository, PaymentPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity jpaEntity = mapper.toJpaEntity(payment);
        PaymentJpaEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        return repository.findById(paymentId.toString()).map(mapper::toDomainEntity);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).map(mapper::toDomainEntity);
    }
}

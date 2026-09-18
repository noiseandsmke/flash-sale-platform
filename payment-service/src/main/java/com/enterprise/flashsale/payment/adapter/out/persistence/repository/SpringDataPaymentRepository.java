package com.enterprise.flashsale.payment.adapter.out.persistence.repository;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, String> {
    Optional<PaymentJpaEntity> findByOrderId(String orderId);
}

package com.enterprise.flashsale.payment.adapter.out.persistence.repository;

import com.enterprise.flashsale.payment.adapter.out.persistence.entity.OutboxJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataOutboxRepository extends JpaRepository<OutboxJpaEntity, String> {
    @Query("SELECT o FROM OutboxJpaEntity o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<OutboxJpaEntity> findPendingEvents(Pageable pageable);
}

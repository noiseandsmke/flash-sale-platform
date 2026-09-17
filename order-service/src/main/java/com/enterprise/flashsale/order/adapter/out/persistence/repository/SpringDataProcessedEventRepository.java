package com.enterprise.flashsale.order.adapter.out.persistence.repository;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.ProcessedEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProcessedEventRepository extends JpaRepository<ProcessedEventJpaEntity, String> {}

package com.enterprise.flashsale.order.adapter.out.persistence.repository;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, String> {}

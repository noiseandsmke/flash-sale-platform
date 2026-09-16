package com.enterprise.flashsale.order.adapter.out.persistence;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.enterprise.flashsale.order.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.enterprise.flashsale.order.adapter.out.persistence.repository.SpringDataOrderRepository;
import com.enterprise.flashsale.order.application.port.out.OrderPersistencePort;
import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PostgresOrderPersistenceAdapter implements OrderPersistencePort {
    private final SpringDataOrderRepository repository;
    private final OrderPersistenceMapper mapper;

    public PostgresOrderPersistenceAdapter(SpringDataOrderRepository repository, OrderPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity jpaEntity = mapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return repository.findById(orderId.toString()).map(mapper::toDomainEntity);
    }
}

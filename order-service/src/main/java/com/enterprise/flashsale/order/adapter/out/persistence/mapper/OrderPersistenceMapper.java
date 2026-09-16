package com.enterprise.flashsale.order.adapter.out.persistence.mapper;

import com.enterprise.flashsale.order.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.enterprise.flashsale.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.enterprise.flashsale.order.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceMapper {
    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId().toString(),
                order.getUserId(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency().getCurrencyCode(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt());

        for (OrderItem item : order.getItems()) {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                    item.ticketId(),
                    item.eventId(),
                    item.price().amount(),
                    item.price().currency().getCurrencyCode(),
                    order.getCreatedAt());
            entity.addItem(itemEntity);
        }

        return entity;
    }

    public Order toDomainEntity(OrderJpaEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(itemEntity -> new OrderItem(
                        itemEntity.getTicketId(),
                        itemEntity.getEventId(),
                        Money.of(
                                itemEntity.getPriceAmount(),
                                java.util.Currency.getInstance(itemEntity.getPriceCurrency()))))
                .toList();

        return new Order(
                OrderId.fromString(entity.getId()),
                entity.getUserId(),
                domainItems,
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

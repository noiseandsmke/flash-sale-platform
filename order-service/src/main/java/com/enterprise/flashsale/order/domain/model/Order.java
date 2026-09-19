package com.enterprise.flashsale.order.domain.model;

import com.enterprise.flashsale.order.domain.event.OrderCancelledEvent;
import com.enterprise.flashsale.order.domain.event.OrderCreatedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final OrderId id;
    private final String userId;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private final Instant createdAt;
    private OrderStatus status;
    private Instant updatedAt;

    public Order(
            OrderId id,
            String userId,
            List<OrderItem> items,
            OrderStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "Order ID must not be null");
        this.userId = Objects.requireNonNull(userId, "User ID must not be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Order items must not be null"));
        this.status = Objects.requireNonNull(status, "Order status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated timestamp must not be null");

        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.totalAmount = calculateTotal(this.items);
    }

    public static Order create(OrderId id, String userId, List<OrderItem> items) {
        Instant now = Instant.now();
        return new Order(id, userId, items, OrderStatus.PENDING_PAYMENT, now, now);
    }

    public OrderCreatedEvent toCreatedEvent() {
        return OrderCreatedEvent.from(this.id, this.userId, this.totalAmount);
    }

    public void markAsPaid() {
        this.status = this.status.transitionTo(OrderStatus.PAID);
        this.updatedAt = Instant.now();
    }

    public OrderCancelledEvent cancel(String reason) {
        this.status = this.status.transitionTo(OrderStatus.CANCELLED);
        this.updatedAt = Instant.now();
        List<Long> ticketIds = this.items.stream().map(OrderItem::ticketId).toList();
        return OrderCancelledEvent.of(this.id.toString(), this.userId, ticketIds, reason);
    }

    private Money calculateTotal(List<OrderItem> items) {
        Money runningTotal = Money.of(0.0, items.getFirst().price().currency().getCurrencyCode());
        for (OrderItem item : items) {
            runningTotal = runningTotal.add(item.price());
        }
        return runningTotal;
    }

    public OrderId getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

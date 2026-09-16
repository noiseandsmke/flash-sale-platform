package com.enterprise.flashsale.order.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "price_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", length = 3, nullable = false)
    private String priceCurrency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public OrderItemJpaEntity() {}

    public OrderItemJpaEntity(
            Long ticketId, Long eventId, BigDecimal priceAmount, String priceCurrency, Instant createdAt) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderJpaEntity getOrder() {
        return order;
    }

    public void setOrder(OrderJpaEntity order) {
        this.order = order;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

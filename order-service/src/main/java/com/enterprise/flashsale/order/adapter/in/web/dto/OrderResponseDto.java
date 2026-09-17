package com.enterprise.flashsale.order.adapter.in.web.dto;

import com.enterprise.flashsale.order.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
        String orderId,
        String userId,
        BigDecimal totalAmount,
        String currency,
        String status,
        Instant createdAt,
        List<OrderItemResponseDto> items) {
    public static OrderResponseDto fromDomain(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.ticketId(),
                        item.eventId(),
                        item.price().amount(),
                        item.price().currency().getCurrencyCode()))
                .toList();

        return new OrderResponseDto(
                order.getId().toString(),
                order.getUserId(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency().getCurrencyCode(),
                order.getStatus().name(),
                order.getCreatedAt(),
                itemDtos);
    }

    public record OrderItemResponseDto(Long ticketId, Long eventId, BigDecimal price, String currency) {}
}

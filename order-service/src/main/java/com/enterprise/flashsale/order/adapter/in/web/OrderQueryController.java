package com.enterprise.flashsale.order.adapter.in.web;

import com.enterprise.flashsale.order.adapter.in.web.dto.ApiResponse;
import com.enterprise.flashsale.order.adapter.in.web.dto.OrderResponseDto;
import com.enterprise.flashsale.order.application.port.in.GetOrderQueryUseCase;
import com.enterprise.flashsale.order.domain.model.Order;
import com.enterprise.flashsale.order.domain.model.OrderId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    private final GetOrderQueryUseCase getOrderQueryUseCase;

    public OrderQueryController(GetOrderQueryUseCase getOrderQueryUseCase) {
        this.getOrderQueryUseCase = getOrderQueryUseCase;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(@PathVariable String orderId) {
        Order order = getOrderQueryUseCase.getOrderById(OrderId.fromString(orderId));
        OrderResponseDto responseDto = OrderResponseDto.fromDomain(order);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}

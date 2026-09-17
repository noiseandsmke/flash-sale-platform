package com.enterprise.flashsale.order.adapter.in.web.dto;

import java.time.Instant;

public record ApiResponse<T>(boolean success, String code, String message, T data, Instant timestamp) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, "SUCCESS", message, data, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Execution successful");
    }
}

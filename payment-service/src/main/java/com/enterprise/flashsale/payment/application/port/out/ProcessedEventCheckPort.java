package com.enterprise.flashsale.payment.application.port.out;

import java.util.UUID;

public interface ProcessedEventCheckPort {
    boolean isProcessed(UUID eventId);

    void markAsProcessed(UUID eventId, String eventType);
}

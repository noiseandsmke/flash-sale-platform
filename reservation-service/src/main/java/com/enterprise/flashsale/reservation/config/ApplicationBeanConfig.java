package com.enterprise.flashsale.reservation.config;

import com.enterprise.flashsale.reservation.application.port.in.ReserveTicketUseCase;
import com.enterprise.flashsale.reservation.application.port.out.TicketEventPublisherPort;
import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.application.service.ReserveTicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public ReserveTicketUseCase reserveTicketUseCase(
            TicketInventoryPort ticketInventoryPort,
            TicketEventPublisherPort ticketEventPublisherPort,
            @Autowired(required = false) MeterRegistry meterRegistry) {
        return new ReserveTicketService(ticketInventoryPort, ticketEventPublisherPort, meterRegistry);
    }
}

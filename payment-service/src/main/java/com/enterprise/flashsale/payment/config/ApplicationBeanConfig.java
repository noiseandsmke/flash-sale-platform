package com.enterprise.flashsale.payment.config;

import com.enterprise.flashsale.payment.application.port.in.ProcessPaymentUseCase;
import com.enterprise.flashsale.payment.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.PaymentGatewayPort;
import com.enterprise.flashsale.payment.application.port.out.PaymentPersistencePort;
import com.enterprise.flashsale.payment.application.port.out.ProcessedEventCheckPort;
import com.enterprise.flashsale.payment.application.service.ProcessPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
    public ProcessPaymentUseCase processPaymentUseCase(
            PaymentPersistencePort paymentPersistencePort,
            OutboxPersistencePort outboxPersistencePort,
            ProcessedEventCheckPort processedEventCheckPort,
            PaymentGatewayPort paymentGatewayPort,
            PlatformTransactionManager transactionManager) {
        ProcessPaymentService coreService = new ProcessPaymentService(
                paymentPersistencePort, outboxPersistencePort, processedEventCheckPort, paymentGatewayPort);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return command -> transactionTemplate.execute(status -> coreService.execute(command));
    }
}

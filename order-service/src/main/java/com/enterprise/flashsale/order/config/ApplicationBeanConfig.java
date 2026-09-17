package com.enterprise.flashsale.order.config;

import com.enterprise.flashsale.order.application.port.in.CreateOrderUseCase;
import com.enterprise.flashsale.order.application.port.in.GetOrderQueryUseCase;
import com.enterprise.flashsale.order.application.port.out.OrderPersistencePort;
import com.enterprise.flashsale.order.application.port.out.OutboxPersistencePort;
import com.enterprise.flashsale.order.application.port.out.ProcessedEventCheckPort;
import com.enterprise.flashsale.order.application.service.CreateOrderService;
import com.enterprise.flashsale.order.application.service.OrderQueryService;
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
    public CreateOrderUseCase createOrderUseCase(
            OrderPersistencePort orderPersistencePort,
            OutboxPersistencePort outboxPersistencePort,
            ProcessedEventCheckPort processedEventCheckPort,
            PlatformTransactionManager transactionManager) {
        CreateOrderService coreService =
                new CreateOrderService(orderPersistencePort, outboxPersistencePort, processedEventCheckPort);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        return command -> transactionTemplate.execute(status -> coreService.execute(command));
    }

    @Bean
    public GetOrderQueryUseCase getOrderQueryUseCase(OrderPersistencePort orderPersistencePort) {
        return new OrderQueryService(orderPersistencePort);
    }
}

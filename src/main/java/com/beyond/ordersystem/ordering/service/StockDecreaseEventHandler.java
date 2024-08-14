package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.common.configs.RabbitMqConfig;
import com.beyond.ordersystem.common.dto.StockDecreaseEvent;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Component
public class StockDecreaseEventHandler {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private ProductRepository productRepository;

    public StockDecreaseEventHandler(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, ProductRepository productRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.productRepository = productRepository;
    }

    public void publish(StockDecreaseEvent stockDecreaseEvent) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.STOCK_DECREASE_QUEUE, stockDecreaseEvent);
    }

    //  Transaction 이 완료된 후에 다음 메시지 수신하므로 동시성 이유 발생X
    @Transactional
    @RabbitListener(queues = RabbitMqConfig.STOCK_DECREASE_QUEUE)
    public void listen(Message message) {
        String messageBody = new String(message.getBody());
        //  json 메시지를 ObjectMapper 로 직접 parsing
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            StockDecreaseEvent stockDecreaseEvent = objectMapper
                    .readValue(messageBody, StockDecreaseEvent.class);
            Product product = productRepository.findById(stockDecreaseEvent.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("not found"));
            product.updateStockQuantity(stockDecreaseEvent.getProductCount());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //  재고 update
    }
}

package com.plgdhd.orderservice.kafka;

import com.plgdhd.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    public OrderProducer(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderCreatedEvent event){
        kafkaTemplate.send("create-order-topic", event.getOrderId(), event);
        log.info("Event sent to payment service");
    }
}

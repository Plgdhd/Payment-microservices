package com.plgdhd.orderservice.kafka;

import com.plgdhd.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    public OrderProducer(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderCreatedEvent event){
        kafkaTemplate.send("create-order-topic", event.getOrderId(), event);
        System.out.println("Event sent to payment service");
    }
}

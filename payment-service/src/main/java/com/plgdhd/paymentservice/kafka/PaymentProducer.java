package com.plgdhd.paymentservice.kafka;

import com.plgdhd.paymentservice.event.PaymentCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    public PaymentProducer(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentEvent(PaymentCreatedEvent event){
        kafkaTemplate.send("create-payment-topic", event.getOrderId(), event);
    }

}

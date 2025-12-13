package com.plgdhd.paymentservice.kafka;

import com.plgdhd.paymentservice.event.OrderCreatedEvent;
import com.plgdhd.paymentservice.event.PaymentCreatedEvent;
import com.plgdhd.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;
    private final PaymentService paymentService;

    @Autowired
    public PaymentConsumer(PaymentProducer paymentProducer,
                           PaymentService paymentService) {
        this.paymentProducer = paymentProducer;
        this.paymentService = paymentService;
    }

//    @KafkaListener(
//            topics = "create-order-topic",
//            groupId = "payment-service-group"
//    )
//    public void handleOrderCreated(OrderCreatedEvent event) {
//        //TODO Logingggg
//        System.out.println("Received order event: " + event.getOrderId());
//
//        //TODO mapper
//        // paymentService.createPayment();
//
//        paymentProducer.sendPaymentEvent(PaymentCreatedEvent.builder()
//                        .orderId(event.getOrderId())
//                        .status(event.getS)
////                .paymentId(event.get)
//        );
//    }
}

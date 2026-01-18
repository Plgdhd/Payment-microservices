package com.plgdhd.paymentservice.kafka;

import com.plgdhd.paymentservice.event.OrderCreatedEvent;
import com.plgdhd.paymentservice.event.PaymentCreatedEvent;
import com.plgdhd.paymentservice.mapper.PaymentMapper;
import com.plgdhd.paymentservice.model.dto.PaymentDto;
import com.plgdhd.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentConsumer(PaymentProducer paymentProducer,
                           PaymentService paymentService,
                           PaymentMapper paymentMapper) {
        this.paymentProducer = paymentProducer;
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @KafkaListener(
            topics = "create-order-topic",
            groupId = "payment-service-group"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        PaymentDto paymentDto = paymentService.createPayment(paymentMapper.toDto(event));

        log.info("Created payment for order: " + paymentDto.getOrderId() + " \nwith status: " + paymentDto.getStatus());
        paymentProducer.sendPaymentEvent(PaymentCreatedEvent.builder()
                        .orderId(event.getOrderId())
                        .status(paymentDto.getStatus())
                        .build()
        );
    }
}

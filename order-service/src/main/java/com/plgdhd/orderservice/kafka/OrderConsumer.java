package com.plgdhd.orderservice.kafka;

import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.event.PaymentCreatedEvent;
import com.plgdhd.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OrderService orderService;

    @Autowired
    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }
    @KafkaListener(
            topics = "create-payment-topic",
            groupId = "order-service-group"
    )
    public void handlePaymentCreated(PaymentCreatedEvent event){
        System.out.println("Payment completed for order with id: " + event.getOrderId() +
                " status: " + event.getStatus());

        if(OrderStatus.COMPLETED.name().equals(event.getStatus())){
            orderService.changeOrderStatus(event.getOrderId(), OrderStatus.COMPLETED);
        }
        else{
            orderService.changeOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
        }
    }
}

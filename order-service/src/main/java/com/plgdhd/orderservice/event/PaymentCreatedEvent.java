package com.plgdhd.orderservice.event;

import lombok.Data;

@Data
public class PaymentCreatedEvent {
    private Long orderId;
    private String status;
}

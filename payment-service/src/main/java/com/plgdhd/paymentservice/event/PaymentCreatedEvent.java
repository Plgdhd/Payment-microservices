package com.plgdhd.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaymentCreatedEvent {
    private Long orderId;
    private String status;
}

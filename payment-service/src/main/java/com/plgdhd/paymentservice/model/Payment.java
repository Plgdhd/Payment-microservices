package com.plgdhd.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    @Indexed
    @Field(name = "order_id")
    private Long orderId;

    @Indexed
    @Field(name = "user_id")
    private Long userId;

    @Indexed
    private String status;

    @Indexed
    private Instant timestamp;

    @Indexed
    @Field(name = "payment_amount")
    private BigDecimal paymentAmount;
}

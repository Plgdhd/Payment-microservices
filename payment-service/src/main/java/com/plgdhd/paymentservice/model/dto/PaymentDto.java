package com.plgdhd.paymentservice.model.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
//    private String id;
    @NotNull
    private Long orderId;
    @NotNull
    private Long userId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal paymentAmount;
}

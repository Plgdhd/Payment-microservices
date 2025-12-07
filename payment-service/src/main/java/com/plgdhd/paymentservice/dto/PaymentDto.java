package com.plgdhd.paymentservice.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private String id;
    @NotBlank
    private Long orderId;
    @NotBlank
    private Long userId;
    private String status;
    private Instant timestamp;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal paymentAmount;
}

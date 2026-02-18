package com.plgdhd.paymentservice.model.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import org.checkerframework.checker.units.qual.N;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    @NotBlank
    private String id;
    @NotNull
    private Long orderId;
    @NotNull
    private Long userId;

    @NotNull
    private String status;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal paymentAmount;
}

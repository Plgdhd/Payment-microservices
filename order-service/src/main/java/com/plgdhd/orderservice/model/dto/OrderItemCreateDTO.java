package com.plgdhd.orderservice.model.dto;

import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemCreateDTO {

    @NotNull(message = "Item id is required")
    @Positive(message = "Item id must be positive")
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00", message = "quantity must be > 0 ")
    private BigDecimal quantity;
}

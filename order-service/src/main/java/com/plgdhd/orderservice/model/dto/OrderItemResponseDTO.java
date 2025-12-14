package com.plgdhd.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long itemId;
    private String itemName;
    private Long itemPrice;
    private BigDecimal quantity;
}
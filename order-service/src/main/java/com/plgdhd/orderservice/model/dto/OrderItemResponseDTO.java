package com.plgdhd.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long itemId;
    private String itemName;
    private Long itemPrice;
    private Integer quantity;
}
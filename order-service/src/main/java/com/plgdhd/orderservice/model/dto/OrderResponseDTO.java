package com.plgdhd.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String status;
    private LocalDate creationDate;

    private UserInfoDTO user;

    private List<OrderItemResponseDTO> items;

}

package com.plgdhd.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@AllArgsConstructor
@Data
public class CardInfoResponseDTO
{
    private Long id;

    @NotBlank(message = "Card number is required")
    @Size(min = 8, max = 32, message = "Card number must be from 8 to 32 symbols")
    private  String number;

    @NotBlank(message = "Card expiration time is required")
    private LocalTime expirationTime;

    @NotBlank(message = "Holder of card is required")
    private String holder;

    @NotBlank(message = "Users id is required")
    private long userId;
}

package com.plgdhd.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
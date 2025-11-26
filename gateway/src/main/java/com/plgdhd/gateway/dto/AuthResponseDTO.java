package com.plgdhd.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponseDTO {
    private Long id; //TODO
    private String username;
    private String role;
    private LocalDateTime createdAt;
}

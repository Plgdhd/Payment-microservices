package com.plgdhd.authenticationservice.dto;

import com.plgdhd.authenticationservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
    private Long id; //TODO
    private String username;
    private Role role;
    private LocalDateTime createdAt;
}
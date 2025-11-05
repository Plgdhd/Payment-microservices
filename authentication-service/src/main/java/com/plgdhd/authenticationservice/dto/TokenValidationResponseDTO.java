package com.plgdhd.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenValidationResponseDTO {
    private Boolean valid;
    private String username;
    private String role;

    public static TokenValidationResponseDTO success(String username, String role) {
        return new TokenValidationResponseDTO(true, username, role);
    }

    public static TokenValidationResponseDTO invalid() {
        return new TokenValidationResponseDTO(false, null, null);
    }
}

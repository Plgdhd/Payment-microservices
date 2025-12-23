package com.plgdhd.gateway.dto;

import lombok.Data;

@Data
public class RegistrationRequestDTO {

    private AuthRequestDTO authData;
    private UserProfileDTO userData;

}

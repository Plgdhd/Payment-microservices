package com.plgdhd.gateway.dto;

import lombok.Data;

@Data
public class RegistrationRequest {

    private AuthRequestDTO authData;
    private UserProfileDTO userData;

}

package com.plgdhd.authenticationservice.mapper;

import com.plgdhd.authenticationservice.dto.AuthRequestDTO;
import com.plgdhd.authenticationservice.dto.RegisterRequestDTO;
import com.plgdhd.authenticationservice.dto.RegisterResponseDTO;
import com.plgdhd.authenticationservice.model.UserCredentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCredentialsMapper {

    RegisterRequestDTO toRegisterRequest(UserCredentials userCredentials);

    RegisterResponseDTO toRegisterResponse(UserCredentials userCredentials);

    AuthRequestDTO toAuthRequest(UserCredentials userCredentials);

    UserCredentials toUserCredentials(AuthRequestDTO registerResponseDTO);

    UserCredentials toUserCredentials(RegisterRequestDTO registerResponseDTO);

}

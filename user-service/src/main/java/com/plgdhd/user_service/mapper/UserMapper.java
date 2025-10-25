package com.plgdhd.user_service.mapper;

import com.plgdhd.user_service.dto.UserRequestDTO;
import com.plgdhd.user_service.dto.UserResponseDTO;
import com.plgdhd.user_service.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);
    UserRequestDTO toRequestDTO(User user);

    User toEntity(UserResponseDTO userDTO);
    User toEntity(UserRequestDTO userDTO);

    List<UserResponseDTO> toDTOList(List<User> users);

}

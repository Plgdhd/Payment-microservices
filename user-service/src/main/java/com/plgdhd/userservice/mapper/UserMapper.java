package com.plgdhd.userservice.mapper;

import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.model.User;
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

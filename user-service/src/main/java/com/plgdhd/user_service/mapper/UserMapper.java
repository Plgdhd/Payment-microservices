package com.plgdhd.user_service.mapper;

import com.plgdhd.user_service.dto.UserDTO;
import com.plgdhd.user_service.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User  toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);
}

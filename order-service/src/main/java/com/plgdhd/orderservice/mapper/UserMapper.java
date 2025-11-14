package com.plgdhd.orderservice.mapper;

import com.plgdhd.orderservice.model.User;
import com.plgdhd.orderservice.model.dto.UserInfoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserInfoDTO toUserDTO(User user);
    User toEntity(UserInfoDTO userDTO);
}

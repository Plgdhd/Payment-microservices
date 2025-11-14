package com.plgdhd.orderservice.client;

import com.plgdhd.orderservice.model.dto.UserInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {

    @Override
    public UserInfoDTO getUserByEmail(String email) {
        return new UserInfoDTO(null, "Service", "Unavailable", null, email);
    }
}
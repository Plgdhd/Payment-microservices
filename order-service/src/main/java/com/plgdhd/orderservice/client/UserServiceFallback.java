package com.plgdhd.orderservice.client;

import com.plgdhd.orderservice.model.dto.UserInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public UserInfoDTO getUserByEmail(String email) {
        //TODO remake fallback
        return new UserInfoDTO(0L, "Service", "Unavailable", null, email);
    }
}
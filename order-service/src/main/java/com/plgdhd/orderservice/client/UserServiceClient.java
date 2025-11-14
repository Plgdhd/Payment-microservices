package com.plgdhd.orderservice.client;

import com.plgdhd.orderservice.model.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/api/users/by-email")
    UserInfoDTO getUserByEmail(@RequestParam("email") String email);
}
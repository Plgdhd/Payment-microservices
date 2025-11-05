package com.plgdhd.userservice.client;

import com.plgdhd.userservice.dto.TokenValidationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url="http://localhost:8081/api/auth")
public interface AuthClient {

    @PostMapping("/validate")
    ResponseEntity<TokenValidationDTO> validateToken(@RequestParam("token") String token);
}

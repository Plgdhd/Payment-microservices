package com.plgdhd.gateway.controller;

import com.plgdhd.gateway.dto.RegistrationRequestDTO;
import com.plgdhd.gateway.dto.UserProfileDTO;
import com.plgdhd.gateway.saga.RegistrationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationOrchestrator registrationOrchestrator;

    @PostMapping("/register-orchestrated")
    public Mono<ResponseEntity<UserProfileDTO>> register(@RequestBody RegistrationRequestDTO request)
    {
        return registrationOrchestrator.register(request)
                .map(ResponseEntity::ok);
    }
}
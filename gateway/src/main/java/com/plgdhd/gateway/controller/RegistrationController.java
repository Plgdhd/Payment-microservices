package com.plgdhd.gateway.controller;

import com.plgdhd.gateway.dto.RegistrationRequestDTO;
import com.plgdhd.gateway.dto.UserProfileDTO;
import com.plgdhd.gateway.saga.RegistrationOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final RegistrationOrchestrator registrationOrchestrator;

    @Autowired
    public RegistrationController(RegistrationOrchestrator registrationOrchestrator) {
        this.registrationOrchestrator = registrationOrchestrator;
    }

    //TODO relocate orchestrated logic to auth service
    @PostMapping("/register-orchestrated")
    public Mono<ResponseEntity<UserProfileDTO>> register(@RequestBody RegistrationRequestDTO request)
    {
        return registrationOrchestrator.register(request)
                .map(ResponseEntity::ok);
    }

}
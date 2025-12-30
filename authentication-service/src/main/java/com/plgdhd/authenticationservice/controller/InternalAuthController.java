package com.plgdhd.authenticationservice.controller;

import com.plgdhd.authenticationservice.dto.ServiceAuthRequest;
import com.plgdhd.authenticationservice.dto.TokenResponseDTO;
import com.plgdhd.authenticationservice.model.Role;
import com.plgdhd.authenticationservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("internal/auth")
public class InternalAuthController {

    private final AuthService authService;

    @Value("${internal.service.secret}")
    private String internalServiceSecret;


    @Autowired
    public InternalAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<TokenResponseDTO>> getServiceToken(@RequestBody ServiceAuthRequest request) {

        if (request.getClientId() != null && internalServiceSecret.equals(request.getClientSecret())) {
            TokenResponseDTO tokenResponse = authService.generateServiceToken(request.getClientId(), Role.SERVICE);

            return Mono.just(ResponseEntity.ok(tokenResponse));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

}

package com.plgdhd.gateway.controller;

import com.plgdhd.gateway.dto.AuthRequestDTO;
import com.plgdhd.gateway.dto.AuthResponseDTO;
import com.plgdhd.gateway.dto.RegistrationRequest;
import com.plgdhd.gateway.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.auth-url}")
    private String authServiceBaseUrl;

    @Value("${services.user-url}")
    private String userServiceBaseUrl;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegistrationRequest request) {

        return webClientBuilder.build()
                .post()
                .uri(authServiceBaseUrl + "/api/auth/register")
                .bodyValue(request.getAuthData())
                .retrieve()
                .bodyToMono(AuthResponseDTO.class)
                .flatMap(authResponse -> {

                    Long createdAuthId = authResponse.getId();

                    UserProfileDTO userProfile = request.getUserData();

                    userProfile.setId(createdAuthId);

                    return webClientBuilder.build()
                            .post()
                            .uri(userServiceBaseUrl + "/api/users")
                            .bodyValue(userProfile)
                            .retrieve()
                            .toBodilessEntity()
                            .map(res -> ResponseEntity.ok("Registration successful"))

                            .onErrorResume(e -> {
                                System.err.println("User Service failed: " + e.getMessage());
                                return webClientBuilder.build()
                                        .delete()
                                        .uri(authServiceBaseUrl + "/api/auth/" + createdAuthId)
                                        .retrieve()
                                        .toBodilessEntity()
                                        .flatMap(rollback -> Mono.error(new RuntimeException("Registration failed. Rollback executed")));
                            });
                });
    }
}
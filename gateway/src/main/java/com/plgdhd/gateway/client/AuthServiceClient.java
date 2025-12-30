package com.plgdhd.gateway.client;

import com.plgdhd.gateway.dto.AuthRequestDTO;
import com.plgdhd.gateway.dto.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Value("${services.auth-service}")
    private String authServiceName;

    public Mono<AuthResponseDTO> register(AuthRequestDTO authRequestDTO) {
        return webClientBuilder.build()
                .post()
                .uri("lb://" + authServiceName + "/api/auth/register")
                .bodyValue(authRequestDTO)
                .retrieve()
                .bodyToMono(AuthResponseDTO.class);
    }

    public Mono<Void> delete(Long userId, String serviceToken) {
        return webClientBuilder.build()
                .delete()
                .uri("lb://" + authServiceName + "/api/auth/" + userId)
                .header("Authorization", "Bearer " + serviceToken)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}

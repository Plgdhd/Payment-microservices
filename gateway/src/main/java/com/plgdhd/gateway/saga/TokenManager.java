package com.plgdhd.gateway.saga;

import com.plgdhd.gateway.dto.TokenResponseDTO; // Или как у тебя называется DTO ответа
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class TokenManager {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TokenManager(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Value("${internal.service.secret}")
    private String clientSecret;

    //TODO connect Redis here
    private String cachedAccessToken;
    private Instant tokenExpiration = Instant.MIN;

    public Mono<String> getToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(tokenExpiration.minusSeconds(300))) {
            return Mono.just(cachedAccessToken);
        }
        return fetchNewToken();
    }

    private Mono<String> fetchNewToken() {
        ServiceAuthRequest requestBody = new ServiceAuthRequest("gateway-service", clientSecret);

        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/internal/auth/token")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .map(response -> {
                    this.cachedAccessToken = response.getAccessToken();
                    this.tokenExpiration = Instant.now().plus(Duration.ofMinutes(60));
                    log.info("Refreshed internal service token");
                    return this.cachedAccessToken;
                })
                .doOnError(e -> log.error("Failed to fetch service token", e));
    }

    private record ServiceAuthRequest(String clientId, String clientSecret) {}
}

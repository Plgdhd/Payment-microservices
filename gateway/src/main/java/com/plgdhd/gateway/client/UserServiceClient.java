package com.plgdhd.gateway.client;

import com.plgdhd.gateway.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Value("${services.user-service}")
    private String userServiceName;

    public Mono<UserProfileDTO> createUserProfile(UserProfileDTO userProfileDTO, String serviceToken) {
        return webClientBuilder.build()
                .post()
                .uri ("lb://" + userServiceName + "/api/users")
                .header("Authorization", "Bearer " + serviceToken)
                .bodyValue(userProfileDTO)
                .retrieve()
                .bodyToMono(UserProfileDTO.class);
    }
}

package com.plgdhd.gateway.saga;

import com.plgdhd.gateway.client.AuthServiceClient;
import com.plgdhd.gateway.client.UserServiceClient;
import com.plgdhd.gateway.dto.RegistrationRequestDTO;
import com.plgdhd.gateway.dto.UserProfileDTO;
import com.plgdhd.gateway.saga.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RegistrationOrchestrator {

    private final AuthServiceClient authServiceClient;
    private final UserServiceClient userServiceClient;
    private final TokenManager serviceTokenManager;

    @Autowired
    public  RegistrationOrchestrator(AuthServiceClient authServiceClient,
                                     UserServiceClient userServiceClient,
                                     TokenManager serviceTokenManager ) {
        this.authServiceClient = authServiceClient;
        this.userServiceClient = userServiceClient;
        this.serviceTokenManager = serviceTokenManager;
    }

    public Mono<UserProfileDTO> register(RegistrationRequestDTO request) {
        return serviceTokenManager.getToken()
                .flatMap(token -> executeRegistrationSaga(request, token));
    }

    private Mono<UserProfileDTO> executeRegistrationSaga(RegistrationRequestDTO request, String serviceToken) {
        return authServiceClient.register(request.getAuthData())
                .doOnNext(authResponse -> log.info("Auth created with ID: {}", authResponse.getId()))
                .flatMap(authResponse -> {
                    Long userId = authResponse.getId();
                    UserProfileDTO userProfileDTO = request.getUserData();
                    userProfileDTO.setId(userId);
                    return userServiceClient.createUserProfile(userProfileDTO, serviceToken)
                            .onErrorResume(error ->
                                    rollbackAuth(userId, serviceToken, error)
                            );
                });
    }

    private Mono<UserProfileDTO> rollbackAuth(Long userId, String serviceToken, Throwable originalError) {
        log.error("Registration failed. Starting rollback for userId: {}", userId, originalError);

        return authServiceClient.delete(userId, serviceToken)
                .then(Mono.error(new RuntimeException(
                        "Registration failed. Rollback executed. Reason: " + originalError.getMessage()
                )));
    }
}

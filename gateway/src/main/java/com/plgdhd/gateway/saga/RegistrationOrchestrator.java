package com.plgdhd.gateway.saga;

import com.plgdhd.gateway.client.AuthServiceClient;
import com.plgdhd.gateway.client.UserServiceClient;
import com.plgdhd.gateway.dto.RegistrationRequestDTO;
import com.plgdhd.gateway.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RegistrationOrchestrator {

    private final AuthServiceClient authServiceClient;
    private final UserServiceClient userServiceClient;

    @Autowired
    public RegistrationOrchestrator(AuthServiceClient authServiceClient,
                                    UserServiceClient userServiceClient) {
        this.authServiceClient = authServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public Mono<UserProfileDTO> register(RegistrationRequestDTO request){
        return authServiceClient.register(request.getAuthData())
                .doOnNext(authResponse -> {
                    System.out.println("AUTH RESPONSE FROM SERVICE: " + authResponse);
                })
                .flatMap(authResponse -> {
                    Long userId = authResponse.getId();
                    UserProfileDTO userProfileDTO = request.getUserData();
                    userProfileDTO.setId(userId);

                    return userServiceClient.createUserProfile(userProfileDTO)
                            .onErrorResume(error ->
                                    userRollback(userId, error));
                });
    }

    private Mono<UserProfileDTO> userRollback(Long userId, Throwable originalError) {
        return authServiceClient.delete(userId)
                .then(Mono.error(new RuntimeException(
                        "Registration failed. Rollback executed: " + originalError.getMessage()
                )));
    }

}

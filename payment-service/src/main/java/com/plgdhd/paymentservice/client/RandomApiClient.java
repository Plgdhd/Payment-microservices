package com.plgdhd.paymentservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class RandomApiClient {
    private final WebClient webClient;

    public RandomApiClient(WebClient.Builder webClientBuilder,
                           @Value("${random.api.url:https://www.randomnumberapi.com}") String randomApiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(randomApiUrl)
                .build();
    }

    public Integer fetchRandomNumber() {
        try {
            return webClient.get()
                    .uri("/api/v1.0/random?min=1&max=100&count=1")
                    .retrieve()
                    .bodyToMono(Integer[].class)
                    .timeout(Duration.ofSeconds(10))
                    .blockOptional()
                    .map(arr -> arr.length > 0 ? arr[0] : 1)
                    .orElse(1);
        } catch (Exception e) {
            System.err.println("Error fetching random number: " + e.getMessage());
            return 1;
        }
    }
}
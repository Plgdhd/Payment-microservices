package com.plgdhd.paymentservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RandomApiClient {
    private final WebClient webClient;
    private final String randomApiUrl;

    public RandomApiClient(WebClient.Builder webClientBuilder,
                          @Value("${random.api.url:https://www.randomnumberapi.com}") String randomApiUrl) {
        this.webClient = webClientBuilder.build();
        this.randomApiUrl = randomApiUrl;
    }

    public Integer fetchRandomNumber() {
        String apiUrl = randomApiUrl + "/api/v1.0/random?min=1&max=100&count=1";
        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(Integer[].class)
                .blockOptional()
                .map(arr -> arr.length > 0 ? arr[0] : 1)
                .orElse(1);
    }
}


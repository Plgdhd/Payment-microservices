package com.plgdhd.paymentservice.client;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Arrays;

@Component
@Slf4j
public class RandomApiClient {
    private final WebClient webClient;

    public RandomApiClient(@Value("${external.random.api:https://www.randomnumberapi.com}") String randomApiUrl) {
        this.webClient = WebClient.create(randomApiUrl);
        log.info("RandomApiClient initialized with URL: {}", randomApiUrl);
    }

    public Integer fetchRandomNumber() {
        try {
            return webClient.get()
                    .uri("/api/v1.0/random?min=1&max=100&count=1")
                    .retrieve()
                    .bodyToMono(Integer[].class)
                    .timeout(Duration.ofSeconds(10))
                    .doOnNext(randomNumbers -> log.info("Received number: " + Arrays.toString(randomNumbers)))
                    .blockOptional()
                    .map(arr -> arr.length > 0 ? arr[0] : 1)
                    .orElse(1);
        } catch (Exception e) {
            log.error("Error fetching random number: " + e.getMessage());
            return 1;
        }
    }
}
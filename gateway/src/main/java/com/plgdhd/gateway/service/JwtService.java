package com.plgdhd.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public Mono<Claims> validateAndGetClaims(String token) {
        return Mono.fromCallable(() ->{
            try{
                return Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getPayload();
            } catch (Exception e){
                throw new RuntimeException("Invalid token" + e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

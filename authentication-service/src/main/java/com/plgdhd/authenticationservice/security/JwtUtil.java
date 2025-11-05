package com.plgdhd.authenticationservice.security;

import com.plgdhd.authenticationservice.dto.TokenValidationResponseDTO;
import com.plgdhd.authenticationservice.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-sec}")
    private long accessExp;

    @Value("${jwt.refresh-expiration-sec}")
    private long refreshExp;

    private Key key;


    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(String username, Role role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExp * 1000);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExp * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenValidationResponseDTO validate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                return TokenValidationResponseDTO.invalid();
            }

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            return TokenValidationResponseDTO.success(username, role);

        } catch (ExpiredJwtException e) {
            return TokenValidationResponseDTO.invalid();
        } catch (JwtException | IllegalArgumentException e) {
            return TokenValidationResponseDTO.invalid();
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}

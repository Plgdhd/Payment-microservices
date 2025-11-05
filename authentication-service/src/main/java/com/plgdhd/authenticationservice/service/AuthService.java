package com.plgdhd.authenticationservice.service;

import com.plgdhd.authenticationservice.dto.*;
import com.plgdhd.authenticationservice.mapper.UserCredentialsMapper;
import com.plgdhd.authenticationservice.model.Role;
import com.plgdhd.authenticationservice.model.UserCredentials;
import com.plgdhd.authenticationservice.repository.UserCredentialsRepository;
import com.plgdhd.authenticationservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserCredentialsMapper userCredentialsMapper;

    @Autowired
    public AuthService(UserCredentialsRepository userCredentialsRepository,
                       PasswordEncoder passwordEncoder,
                       UserCredentialsMapper userCredentialsMapper,
                       JwtUtil jwtUtil) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCredentialsMapper = userCredentialsMapper;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userCredentialsRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserCredentials creds = UserCredentials.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
//                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .role(Role.USER)
                .build();

        return userCredentialsMapper
                .toRegisterResponse(userCredentialsRepository.save(creds));
    }

    public TokenResponseDTO login(AuthRequestDTO authRequestDTO) {
        UserCredentials creds = userCredentialsRepository.findByUsername(authRequestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequestDTO.getPassword(), creds.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String access = jwtUtil.generateAccessToken(creds.getUsername(), creds.getRole());
        String refresh = jwtUtil.generateRefreshToken(creds.getUsername());

        return TokenResponseDTO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .role(creds.getRole())
                .build();
    }

    public TokenResponseDTO refresh(RefreshRequestDTO refreshRequestDTO) {
        if (!jwtUtil.validate(refreshRequestDTO.getRefreshToken()).getValid()) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshRequestDTO.getRefreshToken());
        UserCredentials creds = userCredentialsRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String access = jwtUtil.generateAccessToken(username, creds.getRole());
        String newRefresh = jwtUtil.generateRefreshToken(username);

        return TokenResponseDTO.builder()
                .accessToken(access)
                .refreshToken(newRefresh)
                .role(creds.getRole())
                .build();
    }

    public TokenValidationResponseDTO validate(TokenRequestDTO tokenRequestDTO) {
        return jwtUtil.validate(tokenRequestDTO.getToken());
    }
}

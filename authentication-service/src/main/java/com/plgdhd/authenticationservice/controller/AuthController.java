package com.plgdhd.authenticationservice.controller;

import com.plgdhd.authenticationservice.dto.*;
import com.plgdhd.authenticationservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@Controller
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO dto = authService.register(request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        authService.delete(id);
        return ResponseEntity.ok("Deleted successfully from auth service");
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validate(@RequestParam TokenRequestDTO token) {
        return ResponseEntity.ok(authService.validate(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestParam RefreshRequestDTO refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

}

package com.plgdhd.authenticationservice.controller.advice;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
        return ResponseEntity.badRequest().body("Internal server error: " + ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleUserException(JwtException ex){
        return ResponseEntity.badRequest().body("Internal server error: " + ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserException(UsernameNotFoundException ex){
        return ResponseEntity.badRequest().body("Internal server error: " + ex.getMessage());
    }
}
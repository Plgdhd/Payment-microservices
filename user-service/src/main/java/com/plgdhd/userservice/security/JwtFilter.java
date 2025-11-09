package com.plgdhd.userservice.security;

import com.plgdhd.userservice.client.AuthClient;
import com.plgdhd.userservice.dto.TokenValidationDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final AuthClient authClient;

    @Autowired
    public JwtFilter(AuthClient authClient) {
        this.authClient = authClient;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = header.substring(7);

        try{
            TokenValidationDTO tokenValidationDTO = authClient.validateToken(token).getBody();
            if(tokenValidationDTO == null || !tokenValidationDTO.isValid()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String username = tokenValidationDTO.getUsername();
            String role = tokenValidationDTO.getRole();

            UserDetails userDetails = User
                    .withUsername(username)
                    .password("")
                    .authorities(Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    ))
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
        catch(Exception ex){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

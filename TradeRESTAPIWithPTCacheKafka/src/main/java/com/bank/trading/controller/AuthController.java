package com.bank.trading.controller;

import com.bank.trading.dto.LoginRequest;
import com.bank.trading.dto.LoginResponse;
import com.bank.trading.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthenticationManager authManager;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        Authentication auth =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()));

        List<String> roles =
                auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        String token =
                jwtUtil.generateToken(
                        request.username(),
                        roles);

        return new LoginResponse(token);
    }
}
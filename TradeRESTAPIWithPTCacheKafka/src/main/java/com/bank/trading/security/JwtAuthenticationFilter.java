package com.bank.trading.security;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header == null ||
                !header.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        String token =
                header.substring(7);

        if (!jwtUtil.isValid(token)) {

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        Claims claims =
                jwtUtil.extractClaims(token);

        String username =
                claims.getSubject();

        List<String> roles =
                claims.get("roles", List.class);

        System.out.println("USER = " + claims.getSubject());
        System.out.println("ROLES = " + claims.get("roles"));

        var authorities =
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(
                request,
                response);
    }
}
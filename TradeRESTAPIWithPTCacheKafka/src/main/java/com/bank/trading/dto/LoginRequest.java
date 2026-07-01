package com.bank.trading.dto;

public record LoginRequest(
        String username,
        String password
) {
}
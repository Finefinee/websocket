package com.finefinee.websocket.global.jwt.dto;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}

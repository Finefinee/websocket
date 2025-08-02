package com.finefinee.websocket.global.jwt.dto;

public record AuthRequest(
        String username,
        String password
) {

}

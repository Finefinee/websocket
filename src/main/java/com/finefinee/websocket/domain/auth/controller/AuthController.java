package com.finefinee.websocket.domain.auth.controller;

import com.finefinee.websocket.domain.auth.service.AuthService;
import com.finefinee.websocket.global.jwt.dto.AuthRequest;
import com.finefinee.websocket.global.jwt.dto.JwtResponse;
import com.finefinee.websocket.global.jwt.dto.RefreshRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody AuthRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

}
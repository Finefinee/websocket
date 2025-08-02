package com.finefinee.websocket.global.jwt.service;

import com.finefinee.websocket.domain.member.repository.MemberRepository;
import com.finefinee.websocket.global.jwt.dto.AuthRequest;
import com.finefinee.websocket.global.jwt.provider.JwtProvider;
import com.finefinee.websocket.global.jwt.refresh.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void signup(AuthRequest request) {
        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        MemberEntity member = MemberEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        MemberEntity memberEntity = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), memberEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String username = request.getUsername();

        String accessToken = jwtProvider.generateAccessToken(username);

        String refreshToken = jwtProvider.generateRefreshToken(username);

        refreshTokenRepository.deleteById(username);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .token(passwordEncoder.encode(refreshToken))
                .expiry(jwtProvider.getExpire(refreshToken))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {

        String refreshToken = request.refreshToken();
        String username = jwtProvider.getUsername(refreshToken);

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰입니다."));

        if (!passwordEncoder.matches(refreshToken, savedRefreshToken.getToken())) {
            throw new RuntimeException("유효하지 않은 토큰");
        }

        jwtProvider.validateToken(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(savedRefreshToken.getUsername());

        return new AuthResponse(newAccessToken, refreshToken);

    }
}

package com.finefinee.websocket.domain.auth.service;

import com.finefinee.websocket.domain.member.entity.MemberEntity;
import com.finefinee.websocket.domain.member.repository.MemberRepository;
import com.finefinee.websocket.global.jwt.dto.AuthRequest;
import com.finefinee.websocket.global.jwt.dto.JwtResponse;
import com.finefinee.websocket.global.jwt.dto.RefreshRequest;
import com.finefinee.websocket.global.jwt.provider.JwtProvider;
import com.finefinee.websocket.global.jwt.refresh.RefreshTokenEntity;
import com.finefinee.websocket.global.jwt.refresh.RefreshTokenRepository;
import jakarta.transaction.Transactional;
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
        if (memberRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        MemberEntity member = MemberEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public JwtResponse login(AuthRequest request) {
        MemberEntity memberEntity = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        if (!passwordEncoder.matches(request.password(), memberEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String username = request.username();

        return jwtProvider.generateToken(username);
    }

    @Transactional
    public JwtResponse refresh(RefreshRequest request) {

        String refreshToken = request.refreshToken();
        String username = jwtProvider.getUsername(refreshToken);

        RefreshTokenEntity savedRefreshToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰입니다."));

        if (!passwordEncoder.matches(refreshToken, savedRefreshToken.getToken())) {
            throw new RuntimeException("유효하지 않은 토큰");
        }

        jwtProvider.validateToken(refreshToken);

        return jwtProvider.generateToken(savedRefreshToken.getUsername());

    }
}

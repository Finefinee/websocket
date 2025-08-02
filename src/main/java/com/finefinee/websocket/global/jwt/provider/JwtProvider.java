package com.finefinee.websocket.global.jwt.provider;

import com.finefinee.websocket.global.jwt.dto.JwtResponse;
import com.finefinee.websocket.global.jwt.refresh.RefreshTokenEntity;
import com.finefinee.websocket.global.jwt.refresh.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    private final String secretString = "qon293ufvjwjkovlvdnwj2od0visu3jvkxkkvikai3i3ig9dkmb";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

    @Transactional
    public JwtResponse generateToken(String username) {
        String accessToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(secretKey)
                .compact();

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                        .username(username).token(refreshToken).expiry(getExpire(refreshToken)).build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getExpire(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

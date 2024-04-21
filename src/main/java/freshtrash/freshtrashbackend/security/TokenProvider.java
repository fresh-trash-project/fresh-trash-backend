package freshtrash.freshtrashbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.dto.properties.JwtProperties;
import freshtrash.freshtrashbackend.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ID = "id";
    private final ObjectMapper mapper;
    private final JwtProperties jwtProperties;

    /**
     * 토큰 발급
     */
    public String generateAccessToken(Long memberId) {
        Map<String, Long> claims = new HashMap<>();
        claims.put(KEY_ID, memberId);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessExpiredMs()))
                .signWith(getKey(jwtProperties.secretKey()))
                .compact();
    }

    public Long getMemberIdFromToken(String token) {
        return parseOrValidateClaims(token).get(KEY_ID, Long.class);
    }

    /**
     * 토큰 파싱
     */
    private Claims parseOrValidateClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey(jwtProperties.secretKey()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthException("Expired token", e);
        }
    }

    private SecretKey getKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}

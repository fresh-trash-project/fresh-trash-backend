package freshtrash.freshtrashbackend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.dto.properties.JwtProperties;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Address;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_SPEC = "spec";
    private static final String KEY_RATING = "rating";
    private static final String KEY_FILENAME = "fileName";
    private static final String KEY_ADDRESS = "address";
    private final ObjectMapper mapper;
    private final JwtProperties jwtProperties;

    /**
     * 토큰 발급
     */
    public String generateAccessToken(
            String email, String nickname, String spec, double rating, String fileName, Address address) {
        try {
            Map<String, String> claims = new HashMap<>();
            claims.put(KEY_EMAIL, email);
            claims.put(KEY_NICKNAME, nickname);
            claims.put(KEY_SPEC, spec); // "id:role"
            claims.put(KEY_RATING, String.valueOf(rating));
            claims.put(KEY_FILENAME, fileName);
            claims.put(KEY_ADDRESS, mapper.writeValueAsString(address));

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessExpiredMs()))
                    .signWith(getKey(jwtProperties.secretKey()))
                    .compact();
        } catch (JsonProcessingException e) {
            throw new AuthException("Failed generate token", e);
        }
    }

    /**
     * 토큰 파싱
     */
    public Claims parseOrValidateClaims(String token) {
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

    public TokenInfo parseSpecification(Claims claims) {
        try {
            return TokenInfo.of(
                    claims.get(TokenProvider.KEY_EMAIL, String.class),
                    claims.get(TokenProvider.KEY_NICKNAME, String.class),
                    claims.get(TokenProvider.KEY_SPEC, String.class),
                    Double.parseDouble(claims.get(TokenProvider.KEY_RATING, String.class)),
                    claims.get(TokenProvider.KEY_FILENAME, String.class),
                    mapper.readValue(claims.get(TokenProvider.KEY_ADDRESS, String.class), Address.class));
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("failed token parsing");
            return null;
        }
    }

    /**
     * 토큰에 있는 정보로 UserDetails 생성
     */
    public MemberPrincipal getUserDetails(Claims claims) {
        TokenInfo tokenInfo =
                Optional.ofNullable(claims).map(this::parseSpecification).orElse(TokenInfo.ofAnonymous());
        return tokenInfo.toMemberPrincipal();
    }

    private SecretKey getKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}

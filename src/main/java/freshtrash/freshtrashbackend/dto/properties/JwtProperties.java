package freshtrash.freshtrashbackend.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @param secretKey 토큰을 생성할때 사용되는 키 값
 * @param accessExpiredMs access 토큰 만료 시간 (ms)
 */
@Validated
@ConfigurationProperties(prefix = "jwt.token")
public record JwtProperties(@NotBlank String secretKey, @Positive long accessExpiredMs) {}

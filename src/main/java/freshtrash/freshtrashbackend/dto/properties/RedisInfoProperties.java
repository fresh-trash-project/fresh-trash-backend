package freshtrash.freshtrashbackend.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @param host
 * @param port
 */
@Validated
@ConfigurationProperties(prefix = "data-redis")
public record RedisInfoProperties(@NotBlank String host, int port) {}

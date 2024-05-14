package freshtrash.freshtrashbackend.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "validation")
public record MailProperties(@NotBlank String apiUrl, @NotBlank String apiKey) {}

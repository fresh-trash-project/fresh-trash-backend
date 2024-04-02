package freshtrash.freshtrashbackend.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @param absolutePath 파일을 저장하는 경로
 */
@Validated
@ConfigurationProperties(prefix = "local-file")
public record LocalFileProperties(@NotBlank String absolutePath) {}

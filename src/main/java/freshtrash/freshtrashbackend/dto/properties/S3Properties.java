package freshtrash.freshtrashbackend.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(@NotBlank String bucketName, @NotBlank String accessKey, @NotBlank String secretKey) {}

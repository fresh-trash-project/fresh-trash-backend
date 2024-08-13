package freshtrash.freshtrashbackend.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "rec-sys")
public record RecSysProperties(
        @NotBlank String host,
        @NotBlank String productEndpoint,
        @NotBlank String productPurchase,
        @NotBlank String recommendProduct,
        @Positive @NotNull Integer productLimit) {}

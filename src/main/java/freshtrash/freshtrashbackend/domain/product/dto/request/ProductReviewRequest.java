package freshtrash.freshtrashbackend.domain.product.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public record ProductReviewRequest(@NotNull @Max(5) Integer rate, String content) {}

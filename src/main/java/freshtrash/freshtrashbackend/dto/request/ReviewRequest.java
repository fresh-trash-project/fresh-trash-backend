package freshtrash.freshtrashbackend.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public record ReviewRequest(@NotNull @Max(5) Integer rate, String content) {}

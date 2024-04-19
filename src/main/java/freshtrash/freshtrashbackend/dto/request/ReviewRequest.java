package freshtrash.freshtrashbackend.dto.request;

import javax.validation.constraints.NotNull;

public record ReviewRequest(@NotNull Integer rate) {}

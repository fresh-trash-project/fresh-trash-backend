package freshtrash.freshtrashbackend.dto.request;

import javax.validation.constraints.NotNull;

public record ChatRoomRequest(@NotNull Long buyerId) {}

package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

@Builder
public record MessageRequest(String message, Long wasteId, Long memberId, Long fromMemberId, AlarmType alarmType) {}

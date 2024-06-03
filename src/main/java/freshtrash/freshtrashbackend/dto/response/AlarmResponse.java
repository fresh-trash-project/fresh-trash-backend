package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.entity.AlarmArgs;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AlarmResponse(Long id, AlarmType alarmType, AlarmArgs alarmArgs, String message, LocalDateTime readAt) {
    public static AlarmResponse fromEntity(Alarm alarm) {
        return AlarmResponse.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .alarmArgs(alarm.getAlarmArgs())
                .message(alarm.getMessage())
                .readAt(alarm.getReadAt())
                .build();
    }
}

package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.entity.AlarmArgs;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;

public record AlarmDto(Long id, AlarmType alarmType, AlarmArgs alarmArgs) {
    public static AlarmDto fromEntity(Alarm alarm) {
        return new AlarmDto(alarm.getId(), alarm.getAlarmType(), alarm.getAlarmArgs());
    }
}

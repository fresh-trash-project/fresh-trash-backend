package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.AlarmArgs;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;

public record AlarmResponse(Long id, AlarmType alarmType, AlarmArgs alarmArgs, String message) {
    public static AlarmResponse of(Long id, AlarmType alarmType, AlarmArgs alarmArgs, String message) {
        return new AlarmResponse(id, alarmType, alarmArgs, message);
    }
}

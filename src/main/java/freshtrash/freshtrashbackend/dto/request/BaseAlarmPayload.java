package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Getter;

import javax.persistence.MappedSuperclass;
import java.util.Map;

@Getter
@MappedSuperclass
public abstract class BaseAlarmPayload {
    protected String message;
    protected Long targetId;
    protected Long memberId;
    protected Long fromMemberId;
    protected AlarmType alarmType;

    public abstract Map<String, String> toMap();
}

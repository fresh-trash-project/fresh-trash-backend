package freshtrash.freshtrashbackend.dto.events;

import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmEvent extends BaseEvent<BaseAlarmPayload> {
    public AlarmEvent(String routingKey, BaseAlarmPayload payload) {
        super(routingKey, payload);
    }

    public static AlarmEvent of(String routingKey, BaseAlarmPayload payload) {
        return new AlarmEvent(routingKey, payload);
    }
}

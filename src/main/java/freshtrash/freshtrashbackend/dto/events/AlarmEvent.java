package freshtrash.freshtrashbackend.dto.events;

import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmEvent extends BaseEvent<AlarmPayload> {
    public AlarmEvent(String routingKey, AlarmPayload payload) {
        super(routingKey, payload);
    }

    public static AlarmEvent of(String routingKey, AlarmPayload payload) {
        return new AlarmEvent(routingKey, payload);
    }
}

package freshtrash.freshtrashbackend.dto.events;

import freshtrash.freshtrashbackend.dto.request.MessageRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmEvent extends BaseEvent<MessageRequest> {
    public AlarmEvent(String routingKey, MessageRequest payload) {
        super(routingKey, payload);
    }

    public static AlarmEvent of(String routingKey, MessageRequest payload) {
        return new AlarmEvent(routingKey, payload);
    }
}

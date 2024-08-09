package freshtrash.freshtrashbackend.producer.publisher;

import freshtrash.freshtrashbackend.domain.alarm.dto.events.BaseEvent;
import org.springframework.scheduling.annotation.Async;

public interface MQPublisher {
    @Async
    void publish(BaseEvent<?> event);
}

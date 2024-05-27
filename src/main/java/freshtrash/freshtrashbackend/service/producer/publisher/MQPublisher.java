package freshtrash.freshtrashbackend.service.producer.publisher;

import freshtrash.freshtrashbackend.dto.events.BaseEvent;
import org.springframework.scheduling.annotation.Async;

public interface MQPublisher {
    @Async
    void publish(BaseEvent<?> event);
}

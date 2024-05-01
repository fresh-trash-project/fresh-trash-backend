package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final RabbitMqProducer rabbitMqProducer;

    @Async
    @EventListener(classes = BaseEvent.class)
    public void handleEvent(BaseEvent<?> event) {
        log.debug("handle event: {}", event);
        rabbitMqProducer.publish(event);
    }
}

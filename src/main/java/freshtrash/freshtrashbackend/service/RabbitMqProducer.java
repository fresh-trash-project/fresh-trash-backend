package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publish(BaseEvent<?> event) {
        log.debug("rabbitmq publish: {}", event);
        rabbitTemplate.convertAndSend(event.getRoutingKey(), event.getPayload());
    }
}

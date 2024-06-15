package freshtrash.freshtrashbackend.service.producer.publisher;

import freshtrash.freshtrashbackend.dto.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQPublisher implements MQPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(BaseEvent<?> event) {
        log.debug("by rabbitmq (routingKey: {})", event.getRoutingKey());
        rabbitTemplate.convertAndSend(event.getRoutingKey(), event.getPayload());
    }
}

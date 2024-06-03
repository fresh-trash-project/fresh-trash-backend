package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.constants.QueueType.PRODUCT_TRANSACTION_FLAG;

@Component
@RequiredArgsConstructor
public class ChatProducer {
    private final MQPublisher mqPublisher;

    public void occurredUserFlag(Long productId, Long targetMemberId, Long currentMemberId, String message) {
        mqPublisher.publish(AlarmEvent.of(
                PRODUCT_TRANSACTION_FLAG.getRoutingKey(),
                AlarmPayload.ofUserFlag(message, productId, targetMemberId, currentMemberId)));
    }
}

package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.PRODUCT_TRANSACTION_FLAG;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatProducer {
    private final MQPublisher mqPublisher;

    public void occurredUserFlag(Long productId, Long targetMemberId, Long currentMemberId, String message) {
        log.debug(
                "신고 알람 publish...\n\t=> targetId: {}, targetMemberId: {}, currentMemberId: {}, message: {}",
                productId,
                targetMemberId,
                currentMemberId,
                message);
        mqPublisher.publish(AlarmEvent.of(
                PRODUCT_TRANSACTION_FLAG.getRoutingKey(),
                AlarmPayload.ofUserFlag(message, productId, targetMemberId, currentMemberId)));
    }
}

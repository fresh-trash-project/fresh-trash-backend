package freshtrash.freshtrashbackend.producer;

import freshtrash.freshtrashbackend.domain.alarm.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.domain.alarm.dto.request.ProductAlarmPayload;
import freshtrash.freshtrashbackend.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.global.config.rabbitmq.QueueType.PRODUCT_TRANSACTION_FLAG;

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
                ProductAlarmPayload.ofUserFlag(message, productId, targetMemberId, currentMemberId)));
    }
}

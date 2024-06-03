package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.constants.QueueType.PRODUCT_CHANGE_SELL_STATUS;
import static freshtrash.freshtrashbackend.config.constants.QueueType.PRODUCT_TRANSACTION_COMPLETE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.COMPLETED_SELL_MESSAGE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.REQUEST_REVIEW_MESSAGE;

@Component
@RequiredArgsConstructor
public class ProductDealProducer {
    private final MQPublisher mqPublisher;

    public void completeDeal(ChatRoom chatRoom) {
        mqPublisher.publish(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                AlarmPayload.ofProductDealBySeller(
                        COMPLETED_SELL_MESSAGE.getMessage(), chatRoom, AlarmType.TRANSACTION)));
    }

    public void requestReview(ChatRoom chatRoom) {
        mqPublisher.publish(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                AlarmPayload.ofProductDealByBuyer(
                        REQUEST_REVIEW_MESSAGE.getMessage(), chatRoom, AlarmType.TRANSACTION)));
    }

    public void updateSellStatus(ChatRoom chatRoom, String message, AlarmType alarmType) {
        mqPublisher.publish(AlarmEvent.of(
                PRODUCT_CHANGE_SELL_STATUS.getRoutingKey(),
                AlarmPayload.ofProductDealBySeller(message, chatRoom, alarmType)));
    }
}

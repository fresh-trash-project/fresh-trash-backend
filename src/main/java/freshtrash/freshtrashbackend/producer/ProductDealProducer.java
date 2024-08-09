package freshtrash.freshtrashbackend.producer;

import freshtrash.freshtrashbackend.domain.alarm.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.domain.alarm.dto.request.ProductAlarmPayload;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.global.config.rabbitmq.QueueType.PRODUCT_CHANGE_SELL_STATUS;
import static freshtrash.freshtrashbackend.global.config.rabbitmq.QueueType.PRODUCT_TRANSACTION_COMPLETE;
import static freshtrash.freshtrashbackend.domain.alarm.dto.constants.AlarmMessage.COMPLETED_SELL_MESSAGE;
import static freshtrash.freshtrashbackend.domain.alarm.dto.constants.AlarmMessage.REQUEST_REVIEW_MESSAGE;

@Component
@RequiredArgsConstructor
public class ProductDealProducer {
    private final MQPublisher mqPublisher;

    public void publishForCompletedProductDeal(ChatRoom chatRoom) {
        publishAlarmEvent(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                ProductAlarmPayload.ofCompletedProductDeal(
                        COMPLETED_SELL_MESSAGE.getMessage(), chatRoom, AlarmType.COMPLETE_TRANSACTION)));
    }

    public void publishToBuyerForRequestReview(ChatRoom chatRoom) {
        publishAlarmEvent(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                ProductAlarmPayload.ofRequestReview(
                        REQUEST_REVIEW_MESSAGE.getMessage(), chatRoom, AlarmType.REQUEST_REVIEW)));
    }

    public void publishForUpdatedSellStatus(ChatRoom chatRoom, String message, AlarmType alarmType) {
        publishAlarmEvent(AlarmEvent.of(
                PRODUCT_CHANGE_SELL_STATUS.getRoutingKey(),
                ProductAlarmPayload.ofUpdatedSellStatus(message, chatRoom, alarmType)));
    }

    private void publishAlarmEvent(AlarmEvent alarmEvent) {
        mqPublisher.publish(alarmEvent);
    }
}

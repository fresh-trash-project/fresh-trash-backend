package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.ProductAlarmPayload;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.PRODUCT_CHANGE_SELL_STATUS;
import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.PRODUCT_TRANSACTION_COMPLETE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.COMPLETED_SELL_MESSAGE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.REQUEST_REVIEW_MESSAGE;

@Component
@RequiredArgsConstructor
public class ProductDealProducer {
    private final MQPublisher mqPublisher;

    public void publishForCompletedProductDeal(ChatRoom chatRoom) {
        publishAlarmEvent(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                ProductAlarmPayload.ofCompletedProductDeal(
                        COMPLETED_SELL_MESSAGE.getMessage(), chatRoom, AlarmType.TRANSACTION)));
    }

    public void publishToBuyerForRequestReview(ChatRoom chatRoom) {
        publishAlarmEvent(AlarmEvent.of(
                PRODUCT_TRANSACTION_COMPLETE.getRoutingKey(),
                ProductAlarmPayload.ofRequestReview(
                        REQUEST_REVIEW_MESSAGE.getMessage(), chatRoom, AlarmType.TRANSACTION)));
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

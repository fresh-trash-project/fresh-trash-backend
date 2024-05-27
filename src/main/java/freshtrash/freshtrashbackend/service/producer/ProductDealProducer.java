package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.constants.QueueType.WASTE_CHANGE_SELL_STATUS;
import static freshtrash.freshtrashbackend.config.constants.QueueType.WASTE_TRANSACTION_COMPLETE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.COMPLETED_SELL_MESSAGE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.REQUEST_REVIEW_MESSAGE;

@Component
@RequiredArgsConstructor
public class ProductDealProducer {
    private final MQPublisher mqPublisher;

    public void completeDeal(ChatRoom chatRoom) {
        mqPublisher.publish(generateCompleteDealEvent(chatRoom));
    }

    public void requestReview(ChatRoom chatRoom) {
        mqPublisher.publish(generateRequestReviewEvent(chatRoom));
    }

    public void updateSellStatus(ChatRoom chatRoom, String message) {
        mqPublisher.publish(generateUpdateSellStatusEvent(chatRoom, message));
    }

    private AlarmEvent generateUpdateSellStatusEvent(ChatRoom chatRoom, String message) {
        return AlarmEvent.of(
                WASTE_CHANGE_SELL_STATUS.getRoutingKey(), AlarmPayload.ofProductDealBySeller(message, chatRoom));
    }

    private AlarmEvent generateCompleteDealEvent(ChatRoom chatRoom) {
        return AlarmEvent.of(
                WASTE_TRANSACTION_COMPLETE.getRoutingKey(),
                AlarmPayload.ofProductDealBySeller(COMPLETED_SELL_MESSAGE.getMessage(), chatRoom));
    }

    private AlarmEvent generateRequestReviewEvent(ChatRoom chatRoom) {
        return AlarmEvent.of(
                WASTE_TRANSACTION_COMPLETE.getRoutingKey(),
                AlarmPayload.ofProductDealByBuyer(REQUEST_REVIEW_MESSAGE.getMessage(), chatRoom));
    }
}

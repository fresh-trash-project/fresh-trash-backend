package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.alarm.template.ProductAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompleteDealProductAlarm extends ProductAlarmTemplate {

    public CompleteDealProductAlarm(
            ChatRoomService chatRoomService, ProductDealService productDealService, ProductDealProducer producer) {
        super(chatRoomService, productDealService, producer);
    }

    @Override
    public void update(ChatRoom closedChatRoom) {
        this.productDealService.completeProductDeal(
                closedChatRoom.getProductId(),
                closedChatRoom.getId(),
                closedChatRoom.getSellerId(),
                closedChatRoom.getBuyerId(),
                SellStatus.CLOSE);
    }

    @Override
    public void publishEvent(ChatRoom closedChatRoom) {
        // 판매자, 구매자에게 알람 전송
        log.debug("판매자에게 판매 완료 알림 전송");
        this.producer.publishForCompletedProductDeal(closedChatRoom);
        log.debug("구매자에게 리뷰 요청 알림 전송");
        this.producer.publishToBuyerForRequestReview(closedChatRoom);

        // 그 밖의 채팅 요청한 사용자들에게 알람 전송
        log.debug("채팅 요청했던 사용자들에게 판매 완료 알림 전송");
        this.chatRoomService
                .getNotClosedChatRoomsByProductId(closedChatRoom.getProductId())
                .forEach(this.producer::publishForCompletedProductDeal);
    }
}

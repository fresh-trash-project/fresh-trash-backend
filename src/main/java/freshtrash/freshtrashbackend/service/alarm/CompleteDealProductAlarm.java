package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
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
    void update(ChatRoom closedChatRoom) {
        this.productDealService.completeProductDeal(
                closedChatRoom.getProductId(),
                closedChatRoom.getId(),
                closedChatRoom.getSellerId(),
                closedChatRoom.getBuyerId(),
                SellStatus.CLOSE);
    }

    @Override
    void publishEvent(ChatRoom closedChatRoom) {
        // 판매자, 구매자에게 알람 전송
        this.producer.completeDeal(closedChatRoom);
        log.debug("Send message to seller.");
        this.producer.requestReview(closedChatRoom);
        log.debug("Send message to buyer.");

        // 그 밖의 채팅 요청한 사용자들에게 알람 전송
        chatRoomService
                .getNotClosedChatRoomsByProductId(closedChatRoom.getProductId())
                .forEach(this.producer::completeDeal);
        log.debug("Send message to others.");
    }
}

package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.TransactionService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompleteDealProductAlarm extends ProductAlarmTemplate {

    public CompleteDealProductAlarm(
            ChatRoomService chatRoomService, TransactionService transactionService, ProductDealProducer producer) {
        super(chatRoomService, transactionService, producer);
    }

    @Override
    void update(ChatRoom closedChatRoom) {
        this.transactionService.completeTransaction(
                closedChatRoom.getWasteId(),
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
                .getNotClosedChatRoomsByWasteId(closedChatRoom.getWasteId())
                .forEach(this.producer::completeDeal);
        log.debug("Send message to others.");
    }
}

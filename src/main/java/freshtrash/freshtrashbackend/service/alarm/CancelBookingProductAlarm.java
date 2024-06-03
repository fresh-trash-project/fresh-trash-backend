package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.TransactionService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.UPDATED_ONGOING_MESSAGE;

@Component
public class CancelBookingProductAlarm extends ProductAlarmTemplate {

    public CancelBookingProductAlarm(
            ChatRoomService chatRoomService, TransactionService transactionService, ProductDealProducer producer) {
        super(chatRoomService, transactionService, producer);
    }

    @Override
    void update(ChatRoom chatRoom) {
        this.transactionService.updateSellStatus(chatRoom.getWasteId(), chatRoom.getId(), SellStatus.ONGOING);
    }

    @Override
    void publishEvent(ChatRoom bookedChatRoom) {
        String message = generateMessage(bookedChatRoom.getSeller().getNickname());
        this.chatRoomService
                .getNotClosedChatRoomsByWasteId(bookedChatRoom.getWasteId())
                .forEach(chatRoom -> {
                    this.producer.updateSellStatus(chatRoom, message, AlarmType.TRANSACTION);
                });
    }

    private String generateMessage(String nickname) {
        return String.format(UPDATED_ONGOING_MESSAGE.getMessage(), nickname);
    }
}

package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.TransactionService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.UPDATED_BOOKING_MESSAGE;

@Component
public class RequestBookingProductAlarm extends ProductAlarmTemplate {

    public RequestBookingProductAlarm(
            ChatRoomService chatRoomService, TransactionService transactionService, ProductDealProducer producer) {
        super(chatRoomService, transactionService, producer);
    }

    @Override
    void update(ChatRoom chatRoom) {
        this.transactionService.updateSellStatus(chatRoom.getWasteId(), chatRoom.getId(), SellStatus.BOOKING);
    }

    @Override
    void publishEvent(ChatRoom ongoingChatRoom) {
        String message = generateMessage(ongoingChatRoom.getSeller().getNickname());
        chatRoomService
                .getNotClosedChatRoomsByWasteId(ongoingChatRoom.getWasteId())
                .forEach(chatRoom -> {
                    this.producer.updateSellStatus(chatRoom, message, AlarmType.BOOKING_REQUEST);
                });
    }

    private String generateMessage(String nickname) {
        return String.format(UPDATED_BOOKING_MESSAGE.getMessage(), nickname);
    }
}

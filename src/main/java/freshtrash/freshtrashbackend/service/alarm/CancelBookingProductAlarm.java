package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.alarm.template.ProductAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.UPDATED_ONGOING_MESSAGE;

@Component
public class CancelBookingProductAlarm extends ProductAlarmTemplate {

    public CancelBookingProductAlarm(
            ChatRoomService chatRoomService, ProductDealService productDealService, ProductDealProducer producer) {
        super(chatRoomService, productDealService, producer);
    }

    @Override
    public void update(ChatRoom chatRoom) {
        this.productDealService.updateSellStatus(chatRoom.getProductId(), chatRoom.getId(), SellStatus.ONGOING);
    }

    @Override
    public void publishEvent(ChatRoom bookedChatRoom) {
        String message = generateMessage(bookedChatRoom.getSeller().getNickname());
        this.chatRoomService
                .getNotClosedChatRoomsByProductId(bookedChatRoom.getProductId())
                .forEach(chatRoom -> {
                    this.producer.updateSellStatus(chatRoom, message, AlarmType.TRANSACTION);
                });
    }

    private String generateMessage(String nickname) {
        return String.format(UPDATED_ONGOING_MESSAGE.getMessage(), nickname);
    }
}

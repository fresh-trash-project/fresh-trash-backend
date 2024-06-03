package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.UPDATED_BOOKING_MESSAGE;

@Component
public class RequestBookingProductAlarm extends ProductAlarmTemplate {

    public RequestBookingProductAlarm(
            ChatRoomService chatRoomService, ProductDealService productDealService, ProductDealProducer producer) {
        super(chatRoomService, productDealService, producer);
    }

    @Override
    void update(ChatRoom chatRoom) {
        this.productDealService.updateSellStatus(chatRoom.getProductId(), chatRoom.getId(), SellStatus.BOOKING);
    }

    @Override
    void publishEvent(ChatRoom ongoingChatRoom) {
        String message = generateMessage(ongoingChatRoom.getSeller().getNickname());
        chatRoomService
                .getNotClosedChatRoomsByProductId(ongoingChatRoom.getProductId())
                .forEach(chatRoom -> {
                    this.producer.updateSellStatus(chatRoom, message, AlarmType.BOOKING_REQUEST);
                });
    }

    private String generateMessage(String nickname) {
        return String.format(UPDATED_BOOKING_MESSAGE.getMessage(), nickname);
    }
}

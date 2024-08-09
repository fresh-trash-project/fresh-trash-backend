package freshtrash.freshtrashbackend.domain.alarm.service;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.template.ProductAlarmTemplate;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.chatRoom.entity.constants.ChatRoomSellStatus;
import freshtrash.freshtrashbackend.domain.chatRoom.service.ChatRoomService;
import freshtrash.freshtrashbackend.domain.product.entity.constants.ProductSellStatus;
import freshtrash.freshtrashbackend.domain.product.service.ProductDealService;
import freshtrash.freshtrashbackend.producer.ProductDealProducer;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.domain.alarm.dto.constants.AlarmMessage.UPDATED_ONGOING_MESSAGE;

@Component
public class CancelBookingProductAlarm extends ProductAlarmTemplate {

    public CancelBookingProductAlarm(
            ChatRoomService chatRoomService, ProductDealService productDealService, ProductDealProducer producer) {
        super(chatRoomService, productDealService, producer);
    }

    @Override
    public void update(ChatRoom chatRoom) {
        this.productDealService.updateSellStatus(
                chatRoom.getProductId(), chatRoom.getId(), ProductSellStatus.ONGOING, ChatRoomSellStatus.ONGOING);
    }

    @Override
    public void publishEvent(ChatRoom bookedChatRoom) {
        String message = generateMessage(bookedChatRoom.getSeller().getNickname());
        this.chatRoomService
                .getNotClosedChatRoomsByProductId(bookedChatRoom.getProductId())
                .forEach(chatRoom -> {
                    this.producer.publishForUpdatedSellStatus(chatRoom, message, AlarmType.CANCEL_BOOKING);
                });
    }

    private String generateMessage(String nickname) {
        return String.format(UPDATED_ONGOING_MESSAGE.getMessage(), nickname);
    }

    @Override
    public boolean supports(AlarmType alarmType) {
        return alarmType == AlarmType.CANCEL_BOOKING;
    }
}

package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ProductAlarmTemplate {
    protected final ChatRoomService chatRoomService;
    protected final ProductDealService productDealService;
    protected final ProductDealProducer producer;

    public void sendAlarm(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId, memberId);
        update(chatRoom);
        publishEvent(chatRoom);
    }

    protected abstract void update(ChatRoom chatRoom);

    protected abstract void publishEvent(ChatRoom chatRoom);
}

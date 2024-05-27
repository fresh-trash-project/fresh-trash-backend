package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.TransactionService;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ProductAlarmTemplate {
    protected final ChatRoomService chatRoomService;
    protected final TransactionService transactionService;
    protected final ProductDealProducer producer;

    public void sendAlarm(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        update(chatRoom);
        publishEvent(chatRoom);
    }

    abstract void update(ChatRoom chatRoom);

    abstract void publishEvent(ChatRoom chatRoom);
}

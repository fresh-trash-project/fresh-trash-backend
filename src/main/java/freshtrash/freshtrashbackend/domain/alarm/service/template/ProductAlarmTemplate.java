package freshtrash.freshtrashbackend.domain.alarm.service.template;

import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import freshtrash.freshtrashbackend.domain.chatRoom.service.ChatRoomService;
import freshtrash.freshtrashbackend.domain.product.service.ProductDealService;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.producer.ProductDealProducer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ProductAlarmTemplate implements AlarmTemplate {
    protected final ChatRoomService chatRoomService;
    protected final ProductDealService productDealService;
    protected final ProductDealProducer producer;

    @Override
    public void sendAlarm(AlarmTemplateParameter param) {
        ProductAlarmParameter productAlarmParameter = (ProductAlarmParameter) param;
        ChatRoom chatRoom =
                chatRoomService.getChatRoom(productAlarmParameter.getChatRoomId(), productAlarmParameter.getMemberId());
        update(chatRoom);
        publishEvent(chatRoom);
    }

    protected abstract void update(ChatRoom chatRoom);

    protected abstract void publishEvent(ChatRoom chatRoom);
}

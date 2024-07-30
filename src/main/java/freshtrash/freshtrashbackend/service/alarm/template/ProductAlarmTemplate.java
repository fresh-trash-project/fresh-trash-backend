package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.ChatRoomService;
import freshtrash.freshtrashbackend.service.ProductDealService;
import freshtrash.freshtrashbackend.service.alarm.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.service.producer.ProductDealProducer;
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

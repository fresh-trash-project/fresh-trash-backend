package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.alarm.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.ChatAlarmParameter;
import freshtrash.freshtrashbackend.service.producer.ChatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public abstract class ChatAlarmTemplate implements AlarmTemplate {
    protected final MemberService memberService;
    protected final ChatProducer producer;

    @Override
    public final void sendAlarm(AlarmTemplateParameter param) {
        ChatAlarmParameter chatAlarmParameter = (ChatAlarmParameter) param;
        Long currentMemberId = chatAlarmParameter.getCurrentMemberId();
        ChatRoom chatRoom = chatAlarmParameter.getChatRoom();
        // 알람을 받게될 유저
        Long targetMemberId = Objects.equals(currentMemberId, chatRoom.getSellerId())
                ? chatRoom.getBuyerId()
                : chatRoom.getSellerId();
        log.debug("알람을 받을 유저 memberId: {}", targetMemberId);
        publishEvent(update(targetMemberId), chatRoom.getProductId(), targetMemberId, currentMemberId);
    }

    protected abstract int update(Long targetId);

    protected abstract void publishEvent(int updatedValue, Long productId, Long targetMemberId, Long currentMemberId);
}

package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.producer.ChatProducer;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class ChatAlarmTemplate {
    protected final MemberService memberService;
    protected final ChatProducer producer;

    public final void sendAlarm(ChatRoom chatRoom, Long currentMemberId) {
        // 알람을 받게될 유저
        Long targetMemberId = Objects.equals(currentMemberId, chatRoom.getSellerId())
                ? chatRoom.getBuyerId()
                : chatRoom.getSellerId();
        publishEvent(update(targetMemberId), chatRoom.getProductId(), targetMemberId, currentMemberId);
    }

    abstract int update(Long targetId);

    abstract void publishEvent(int updatedValue, Long productId, Long targetMemberId, Long currentMemberId);
}

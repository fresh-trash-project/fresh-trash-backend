package freshtrash.freshtrashbackend.domain.alarm.service.parameter;

import freshtrash.freshtrashbackend.domain.chatRoom.entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatAlarmParameter implements AlarmTemplateParameter {
    private ChatRoom chatRoom;
    private Long currentMemberId;

    public ChatAlarmParameter(ChatRoom chatRoom, Long currentMemberId) {
        this.chatRoom = chatRoom;
        this.currentMemberId = currentMemberId;
    }
}

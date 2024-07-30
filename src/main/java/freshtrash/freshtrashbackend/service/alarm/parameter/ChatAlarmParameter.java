package freshtrash.freshtrashbackend.service.alarm.parameter;

import freshtrash.freshtrashbackend.entity.ChatRoom;
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

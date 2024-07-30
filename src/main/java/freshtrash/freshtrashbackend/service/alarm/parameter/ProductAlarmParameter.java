package freshtrash.freshtrashbackend.service.alarm.parameter;

import lombok.Getter;

@Getter
public class ProductAlarmParameter implements AlarmTemplateParameter {
    private Long chatRoomId;
    private Long memberId;

    public ProductAlarmParameter(Long chatRoomId, Long memberId) {
        this.chatRoomId = chatRoomId;
        this.memberId = memberId;
    }
}

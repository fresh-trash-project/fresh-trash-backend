package freshtrash.freshtrashbackend.domain.chatRoom.controller.constants;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductEventType {
    CANCEL_BOOKING(AlarmType.CANCEL_BOOKING), // 예약 취소
    REQUEST_BOOKING(AlarmType.REQUEST_BOOKING), // 예약 요청
    COMPLETE_DEAL(AlarmType.COMPLETE_TRANSACTION); // 거래 완료

    private final AlarmType alarmType;
}

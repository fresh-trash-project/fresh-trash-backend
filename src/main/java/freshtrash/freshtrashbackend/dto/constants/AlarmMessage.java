package freshtrash.freshtrashbackend.dto.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmMessage {
    COMPLETED_SELL_MESSAGE("판매 완료되었습니다."),
    REQUEST_REVIEW_MESSAGE("판매 완료되었습니다. 판매자에 대한 리뷰를 작성해주세요."),
    REQUEST_BOOKING_MESSAGE("님의 예약 요청이 왔습니다. 수락 또는 거절을 선택해 주세요."),
    ACCEPT_BOOKING_MESSAGE("님이 예약 요청을 승낙하였습니다."),
    DECLINE_BOOKING_MESSAGE("님이 예약 요청을 거절하였습니다.");
    private final String message;
}

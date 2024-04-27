package freshtrash.freshtrashbackend.dto.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmMessage {
    COMPLETED_SELL_MESSAGE("판매 완료되었습니다."),
    REQUEST_REVIEW_MESSAGE("판매 완료되었습니다. 판매자에 대한 리뷰를 작성해주세요."),
    REQUEST_BOOKING_MESSAGE("님의 예약 요청이 왔습니다. 수락 또는 거절을 선택해 주세요."),
    BOOKING_MESSAGE("님이 예약중으로 판매상태를 변경하였습니다."),
    FLAG_MESSAGE("번 신고받은 내역이 있습니다. 신고받은 횟수가 10번이상 되면 서비스를 이용하실 수 없습니다."),
    EXCEED_FLAG_MESSAGE("10번이상 신고받으셔서 더이상 서비스를 이용하실 수 없습니다.");
    private final String message;
}

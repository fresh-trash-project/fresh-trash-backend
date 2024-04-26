package freshtrash.freshtrashbackend.dto.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmMessage {
    COMPLETED_SELL_MESSAGE("판매 완료되었습니다."),
    REQUEST_REVIEW_MESSAGE("판매 완료되었습니다. 판매자에 대한 리뷰를 작성해주세요."),
    REQUEST_BOOKING_MESSAGE("님의 예약 요청이 왔습니다. 수락 또는 거절을 선택해 주세요."),
    ACCEPT_BOOKING_MESSAGE("님이 예약 요청을 승낙하였습니다."),
    DECLINE_BOOKING_MESSAGE("님이 예약 요청을 거절하였습니다."),
    CANCEL_TRANSACTION_MESSAGE("님이 거래를 취소하였습니다."),
    CANCEL_ALERT_MESSAGE("번 취소내역이 있습니다. 3번이상 거래를 취소하실 경우 서비스를 이용하실 수 없습니다."),
    BLACKLIST_MESSAGE("3번 이상 거래를 취소하여 더이상 서비스를 이용하실 수 없습니다.");
    private final String message;
}

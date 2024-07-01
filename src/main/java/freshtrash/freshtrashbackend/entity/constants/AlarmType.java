package freshtrash.freshtrashbackend.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    TRANSACTION("product_status"), // 거래 상태 변경 시 알림
    BOOKING_REQUEST("product_status"), // 예약 요청 알림
    BIDDING("auction_status"), // 낙찰 알림
    CANCEL("auction_status"), // 경매 취소 알림
    RECEIVE("auction_status"), // 상품 수령 알림
    PAY("pay_status"), // 결제 완료 알림
    NOT_PAY("pay_status"), // 미결제 알림
    FLAG("flag"); // 사용자 신고 알림

    private final String eventName;
}

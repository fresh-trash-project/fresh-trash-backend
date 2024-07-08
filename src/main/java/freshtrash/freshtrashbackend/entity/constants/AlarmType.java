package freshtrash.freshtrashbackend.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    // 중고 거래
    COMPLETE_TRANSACTION("product_status"), // 거래 완료 시 알림
    CANCEL_BOOKING("product_status"), // 예약 취소 시 알림
    REQUEST_BOOKING("product_status"), // 예약 요청 알림
    REQUEST_REVIEW("product_status"), // 중고 거래 리뷰 요청 알림
    // 경매
    BIDDING("auction_status"), // 낙찰 알림
    CANCEL_AUCTION("auction_status"), // 경매 취소 알림
    RECEIVE("auction_status"), // 상품 수령 알림
    PAY("pay_status"), // 결제 완료 알림
    NOT_PAY("pay_status"), // 미결제 알림
    // 신고
    FLAG("flag"); // 사용자 신고 알림

    private final String eventName;
}

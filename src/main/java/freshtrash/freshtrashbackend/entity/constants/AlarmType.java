package freshtrash.freshtrashbackend.entity.constants;

public enum AlarmType {
    TRANSACTION, // 거래 상태 변경 시 알림
    BOOKING_REQUEST, // 예약 요청 알림
    BIDDING, // 낙찰 알림
    CANCEL, // 경매 취소 알림
    PAY, // 결제 완료 알림
    RECEIVE, // 상품 수령 알림
    NOT_PAY, // 미결제 알림
    FLAG // 사용자 신고 알림
}

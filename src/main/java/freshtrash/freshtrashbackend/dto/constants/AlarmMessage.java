package freshtrash.freshtrashbackend.dto.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmMessage {
    // 중고 거래
    COMPLETED_SELL_MESSAGE("✅애물단지 거래가 완료되었습니다."),
    REQUEST_REVIEW_MESSAGE("✅애물단지 거래가 완료되었습니다. 이 알림 메시지를 클릭해서 평점을 남겨주세요."),
    UPDATED_BOOKING_MESSAGE("🛒%s님이 예약중으로 변경하였습니다."),
    UPDATED_ONGOING_MESSAGE("🛒%s님이 판매중으로 변경하였습니다."),
    // 경고
    FLAG_MESSAGE("🚩%d번 경고를 받으셨습니다. 경고가 10번 누적되면 서비스를 이용하실 수 없습니다."),
    EXCEED_FLAG_MESSAGE("🚩경고가 10번 누적되어 더이상 서비스를 이용하실 수 없습니다."),
    // 경매
    NOT_COMPLETED_AUCTION_MESSAGE("🌫️경매 [%s]가 입찰된 내역이 없습니다."),
    COMPLETE_BID_AUCTION_MESSAGE("🎉경매 [%s]가 낙찰되었습니다."),
    REQUEST_PAY_AUCTION_MESSAGE("🎉축하합니다! 경매 [%s]가 낙찰되었습니다. 이 알림 메시지를 클릭하여 결제 페이지로 이동해 결제를 진행해주세요. 24시간 이내에 결제를 완료해주세요."),
    CANCEL_AUCTION_MESSAGE("❌경매 [%s]가 취소되었습니다."),
    REVIEW_FROM_BUYER_MESSAGE("📦작성된 상품 리뷰가 있습니다."),
    // 경매 - 결제
    COMPLETED_PAY_MESSAGE("💳경매 [%s] 결제가 완료되었습니다."),
    COMPLETED_PAY_AND_REQUEST_DELIVERY_MESSAGE("💳경매 [%s] 상품 결제가 완료되었습니다. %s 님에게 상품을 배송해주세요."),
    BUYER_NOT_PAID_MESSAGE("⚠️%s님이 경매 [%s] 상품을 24시간 이내에 결제하지않아 해당 경매 낙찰을 취소합니다."),
    NOT_PAID_MESSAGE("⚠️경매 [%s]의 결제가 완료되지 않았습니다. 24시간 이내에 결제하지않아 경매 낙찰을 취소합니다.");

    private final String message;
}

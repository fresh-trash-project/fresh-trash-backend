package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionAlarmPayload extends BaseAlarmPayload {

    @Override
    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        data.put("auctionId", targetId.toString());
        data.put("memberId", memberId.toString());
        data.put("fromMemberId", fromMemberId.toString());
        data.put("alarmType", alarmType.name());
        return data;
    }

    /**
     * 경매가 입찰되지 않았음을 알림
     */
    public static BaseAlarmPayload ofNotExistBidders(String message, Auction auction) {
        return ofAuction(message, auction, AlarmType.BIDDING)
                .memberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 낙찰되었음을 판매자에게 알림
     */
    public static BaseAlarmPayload ofCompletedAuction(String message, Auction auction, Long fromMemberId) {
        return ofAuction(message, auction, AlarmType.BIDDING)
                .memberId(auction.getMemberId())
                .fromMemberId(fromMemberId)
                .build();
    }

    /**
     * 경매가 낙찰되어 결제 요청을 구매자에게 알림
     */
    public static BaseAlarmPayload ofRequestPay(String message, Auction auction, Long fromMemberId) {
        return ofAuction(message, auction, AlarmType.BIDDING)
                .memberId(fromMemberId)
                .fromMemberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 취소되었음을 입찰자들에게 알림
     */
    public static BaseAlarmPayload ofCancelAuctionToBidders(String message, Auction auction, Long fromMemberId) {
        return ofAuction(message, auction, AlarmType.CANCEL)
                .memberId(fromMemberId)
                .fromMemberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 취소되었음을 판매자에게 알림
     */
    public static BaseAlarmPayload ofCancelAuctionToSeller(String message, Auction auction) {
        return ofAuction(message, auction, AlarmType.CANCEL)
                .memberId(auction.getMemberId())
                .build();
    }

    /**
     * 결제 완료되었음을 낙찰자(결제자)에게 알림
     */
    public static BaseAlarmPayload ofCompletedPayToWonBidder(String message, Auction auction, Long payMemberId) {
        return ofAuction(message, auction, AlarmType.PAY)
                .memberId(payMemberId)
                .fromMemberId(auction.getMemberId())
                .build();
    }

    /**
     * 결제 완료되었음을 판매자에게 알리고 구매자에게 배송 요청
     */
    public static BaseAlarmPayload ofCompletedPayAndRequestDeliveryToSeller(
            String message, Auction auction, Long payMemberId) {
        return ofAuction(message, auction, AlarmType.PAY)
                .memberId(auction.getMemberId())
                .fromMemberId(payMemberId)
                .build();
    }

    private static AuctionAlarmPayloadBuilder ofAuction(String message, Auction auction, AlarmType alarmType) {
        return AuctionAlarmPayload.builder()
                .message(message)
                .targetId(auction.getId())
                .alarmType(alarmType);
    }

    @Builder
    public AuctionAlarmPayload(String message, Long targetId, Long memberId, Long fromMemberId, AlarmType alarmType) {
        this.message = message;
        this.targetId = targetId;
        this.memberId = memberId;
        this.fromMemberId = fromMemberId;
        this.alarmType = alarmType;
    }
}

package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record AlarmPayload(String message, Long targetId, Long memberId, Long fromMemberId, AlarmType alarmType) {
    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        data.put("productId", targetId.toString());
        data.put("memberId", memberId.toString());
        data.put("fromMemberId", fromMemberId.toString());
        data.put("alarmType", alarmType.name());
        return data;
    }

    /**
     * 구매자에의해 구매되었음을 판매자에게 알림
     */
    public static AlarmPayload ofProductDealByBuyer(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getSellerId())
                .fromMemberId(chatRoom.getBuyerId())
                .build();
    }

    /**
     * 판매자가 올린 상품이 거래되었음을 구매자에게 알림
     */
    public static AlarmPayload ofProductDealBySeller(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return ofProductDeal(message, chatRoom, alarmType)
                .memberId(chatRoom.getBuyerId())
                .fromMemberId(chatRoom.getSellerId())
                .build();
    }

    /**
     * 현재 사용자에의해 특정 사용자가 신고되었음을 알림
     */
    public static AlarmPayload ofUserFlag(String message, Long productId, Long targetMemberId, Long currentMemberId) {
        return AlarmPayload.builder()
                .message(message)
                .targetId(productId)
                .memberId(targetMemberId)
                .fromMemberId(currentMemberId)
                .alarmType(AlarmType.FLAG)
                .build();
    }

    /**
     * 경매가 입찰되지 않았음을 알림
     */
    public static AlarmPayload ofAuctionNotBid(String message, Auction auction) {
        return ofAuctionBid(message, auction, AlarmType.BIDDING)
                .memberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 낙찰되었음을 판매자에게 알림
     */
    public static AlarmPayload ofAuctionBidByBuyer(String message, Auction auction, Long fromMemberId) {
        return ofAuctionBid(message, auction, AlarmType.BIDDING)
                .memberId(auction.getMemberId())
                .fromMemberId(fromMemberId)
                .build();
    }

    /**
     * 경매가 낙찰되어 결제 요청을 구매자에게 알림
     */
    public static AlarmPayload ofAuctionBidBySeller(String message, Auction auction, Long fromMemberId) {
        return ofAuctionBid(message, auction, AlarmType.BIDDING)
                .memberId(fromMemberId)
                .fromMemberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 취소되었음을 입찰자들에게 알림
     */
    public static AlarmPayload ofCancelAuction(String message, Auction auction, Long fromMemberId) {
        return ofAuctionBid(message, auction, AlarmType.CANCEL)
                .memberId(fromMemberId)
                .fromMemberId(auction.getMemberId())
                .build();
    }

    /**
     * 경매가 취소되었음을 판매자에게 알림
     */
    public static AlarmPayload ofCancelAuction(String message, Auction auction) {
        return ofAuctionBid(message, auction, AlarmType.CANCEL)
                .memberId(auction.getMemberId())
                .build();
    }

    private static AlarmPayloadBuilder ofProductDeal(String message, ChatRoom chatRoom, AlarmType alarmType) {
        return AlarmPayload.builder()
                .message(message)
                .targetId(chatRoom.getProductId())
                .alarmType(alarmType);
    }

    private static AlarmPayloadBuilder ofAuctionBid(String message, Auction auction, AlarmType alarmType) {
        return AlarmPayload.builder().message(message).targetId(auction.getId()).alarmType(alarmType);
    }
}

package freshtrash.freshtrashbackend.controller.constants;

import freshtrash.freshtrashbackend.entity.constants.AuctionStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionMemberType {
    AUCTION_ONGOING(AuctionStatus.ONGOING), // 판매자 - 진행중인 경매
    AUCTION_CLOSE(AuctionStatus.CLOSE), // 판매자 - 닫힌 경매
    WINNING_BID(AuctionStatus.CLOSE); // 구매자 - 낙찰 내역

    private final AuctionStatus auctionStatus;
}

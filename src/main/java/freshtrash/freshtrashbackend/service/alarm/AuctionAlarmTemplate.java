package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AuctionAlarmTemplate {
    protected final AuctionService auctionService;
    protected final AuctionPublisher producer;

    public final void sendAlarm(Auction auction) {
        update(auction.getId());
        auction.getBiddingHistories().stream()
                .findFirst()
                .ifPresentOrElse(
                        biddingHistory -> publishEvent(auction, biddingHistory.getMemberId()),
                        () -> publishEvent(auction));
    }

    abstract void update(Long targetId);

    abstract void publishEvent(Auction auction, Long bidMemberId);

    abstract void publishEvent(Auction auction);
}

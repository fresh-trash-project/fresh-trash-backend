package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.BiddingHistoryService;
import freshtrash.freshtrashbackend.service.alarm.template.AuctionAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class CompleteBidAuctionAlarm extends AuctionAlarmTemplate {
    private final BiddingHistoryService biddingHistoryService;

    public CompleteBidAuctionAlarm(
            AuctionService auctionService, AuctionProducer producer, BiddingHistoryService biddingHistoryService) {
        super(auctionService, producer);
        this.biddingHistoryService = biddingHistoryService;
    }

    public final void sendAlarm(Auction auction) {
        update(auction.getId());
        auction.getBiddingHistories().stream()
                .findFirst()
                .ifPresentOrElse(
                        biddingHistory -> publishEvent(auction, biddingHistory.getMemberId()),
                        () -> publishEvent(auction));
    }

    @Transactional
    @Override
    public void update(Long auctionId) {
        this.auctionService.closeAuction(auctionId);
        this.biddingHistoryService.updateSuccessBidAt(auctionId);
    }

    @Override
    public void publishEvent(Auction auction, Long bidMemberId) {
        log.debug("판매자에게 낙찰 알림, 구매자에게 결제 요청 알림");
        this.producer.publishToSellerForCompletedAuction(auction, bidMemberId);
        this.producer.publishToWonBidderForRequestPay(auction, bidMemberId);
    }

    @Override
    public void publishEvent(Auction auction) {
        log.debug("입찰자가 없음을 판매자에게 알림");
        this.producer.publishToSellerForNotExistBidders(auction);
    }
}

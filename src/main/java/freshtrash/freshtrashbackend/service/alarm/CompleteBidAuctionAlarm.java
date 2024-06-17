package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.alarm.template.AuctionAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompleteBidAuctionAlarm extends AuctionAlarmTemplate {

    public CompleteBidAuctionAlarm(AuctionService auctionService, AuctionPublisher producer) {
        super(auctionService, producer);
    }

    @Override
    public void update(Long auctionId) {
        this.auctionService.closeAuction(auctionId);
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

package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.alarm.template.AuctionAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelAuctionAlarm extends AuctionAlarmTemplate {

    public CancelAuctionAlarm(AuctionService auctionService, AuctionPublisher producer) {
        super(auctionService, producer);
    }

    @Override
    protected void update(Long targetId) {
        log.debug("경매 취소(삭제) 처리");
        this.auctionService.deleteAuction(targetId);
    }

    @Override
    protected void publishEvent(Auction auction, Long bidMemberId) {
        log.debug("입찰자에게 경매 취소되었음을 알림");
        this.producer.publishToBiddersForCancelAuction(auction, bidMemberId);
    }

    @Override
    protected void publishEvent(Auction auction) {
        log.debug("구매자에게 경매 취소되었음을 알림");
        this.producer.publishToSellerForCancelAuction(auction);
    }
}

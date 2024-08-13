package freshtrash.freshtrashbackend.domain.alarm.service;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.template.AuctionAlarmTemplate;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelAuctionAlarm extends AuctionAlarmTemplate {

    public CancelAuctionAlarm(AuctionService auctionService, AuctionProducer producer) {
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
        log.debug("판매자에게 경매 취소되었음을 알림");
        this.producer.publishToSellerForCancelAuction(auction);
    }

    @Override
    public boolean supports(AlarmType alarmType) {
        return alarmType == AlarmType.CANCEL_AUCTION;
    }
}

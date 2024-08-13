package freshtrash.freshtrashbackend.domain.alarm.service.template;

import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AuctionAlarmParameter;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AuctionAlarmTemplate implements AlarmTemplate {
    protected final AuctionService auctionService;
    protected final AuctionProducer producer;

    @Override
    public void sendAlarm(AlarmTemplateParameter param) {
        AuctionAlarmParameter auctionAlarmParameter = (AuctionAlarmParameter) param;
        Auction auction = auctionAlarmParameter.getAuction();
        update(auction.getId());
        auction.getBiddingHistories().forEach(biddingHistory -> publishEvent(auction, biddingHistory.getMemberId()));
        publishEvent(auction);
    }

    protected abstract void update(Long targetId);

    protected abstract void publishEvent(Auction auction, Long bidMemberId);

    protected abstract void publishEvent(Auction auction);
}

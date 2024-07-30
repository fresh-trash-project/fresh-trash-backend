package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.alarm.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.AuctionAlarmParameter;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
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

package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BiddingHistoryAlarmTemplate {
    protected final AuctionProducer producer;

    public final void sendAlarm(BiddingHistory biddingHistory) {
        update(biddingHistory);
        publishEvent(biddingHistory);
    }

    protected abstract void update(BiddingHistory biddingHistory);

    protected abstract void publishEvent(BiddingHistory biddingHistory);
}

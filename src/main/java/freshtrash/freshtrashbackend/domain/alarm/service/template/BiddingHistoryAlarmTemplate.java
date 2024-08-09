package freshtrash.freshtrashbackend.domain.alarm.service.template;

import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.BiddingHistoryAlarmParameter;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BiddingHistoryAlarmTemplate implements AlarmTemplate {
    protected final AuctionProducer producer;

    @Override
    public final void sendAlarm(AlarmTemplateParameter param) {
        BiddingHistoryAlarmParameter biddingHistoryAlarmParameter = (BiddingHistoryAlarmParameter) param;
        BiddingHistory biddingHistory = biddingHistoryAlarmParameter.getBiddingHistory();
        update(biddingHistory);
        publishEvent(biddingHistory);
    }

    protected abstract void update(BiddingHistory biddingHistory);

    protected abstract void publishEvent(BiddingHistory biddingHistory);
}

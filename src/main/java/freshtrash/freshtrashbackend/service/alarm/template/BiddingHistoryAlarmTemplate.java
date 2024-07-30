package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.alarm.parameter.AlarmTemplateParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.BiddingHistoryAlarmParameter;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
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

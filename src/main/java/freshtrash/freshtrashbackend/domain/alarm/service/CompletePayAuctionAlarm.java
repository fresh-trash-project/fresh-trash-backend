package freshtrash.freshtrashbackend.domain.alarm.service;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.template.BiddingHistoryAlarmTemplate;
import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompletePayAuctionAlarm extends BiddingHistoryAlarmTemplate {

    public CompletePayAuctionAlarm(AuctionProducer producer) {
        super(producer);
    }

    @Override
    public void update(BiddingHistory biddingHistory) {
        log.debug("결제 여부를 true로 업데이트");
        biddingHistory.setPay(true);
    }

    @Override
    public void publishEvent(BiddingHistory biddingHistory) {
        log.debug("결제한 유저와 판매자에게 결제 완료 알림 전송");
        this.producer.publishForCompletedPayAndRequestDelivery(biddingHistory);
    }

    @Override
    public boolean supports(AlarmType alarmType) {
        return alarmType == AlarmType.PAY;
    }
}

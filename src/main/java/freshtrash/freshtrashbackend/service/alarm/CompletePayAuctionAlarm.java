package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.alarm.template.BiddingHistoryAlarmTemplate;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
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
}

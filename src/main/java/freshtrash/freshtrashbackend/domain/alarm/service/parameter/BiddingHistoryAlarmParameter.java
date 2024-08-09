package freshtrash.freshtrashbackend.domain.alarm.service.parameter;

import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import lombok.Getter;

@Getter
public class BiddingHistoryAlarmParameter implements AlarmTemplateParameter {
    private BiddingHistory biddingHistory;

    public BiddingHistoryAlarmParameter(BiddingHistory biddingHistory) {
        this.biddingHistory = biddingHistory;
    }
}

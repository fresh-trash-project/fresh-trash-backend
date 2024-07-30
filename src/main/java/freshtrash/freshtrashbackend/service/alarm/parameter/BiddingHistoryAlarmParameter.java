package freshtrash.freshtrashbackend.service.alarm.parameter;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import lombok.Getter;

@Getter
public class BiddingHistoryAlarmParameter implements AlarmTemplateParameter {
    private BiddingHistory biddingHistory;

    public BiddingHistoryAlarmParameter(BiddingHistory biddingHistory) {
        this.biddingHistory = biddingHistory;
    }
}

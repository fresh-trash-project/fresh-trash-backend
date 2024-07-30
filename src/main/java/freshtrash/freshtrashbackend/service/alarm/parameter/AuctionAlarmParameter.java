package freshtrash.freshtrashbackend.service.alarm.parameter;

import freshtrash.freshtrashbackend.entity.Auction;
import lombok.Getter;

@Getter
public class AuctionAlarmParameter implements AlarmTemplateParameter {
    private Auction auction;

    public AuctionAlarmParameter(Auction auction) {
        this.auction = auction;
    }
}

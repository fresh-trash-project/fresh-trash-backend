package freshtrash.freshtrashbackend.domain.alarm.service.parameter;

import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import lombok.Getter;

@Getter
public class AuctionAlarmParameter implements AlarmTemplateParameter {
    private Auction auction;

    public AuctionAlarmParameter(Auction auction) {
        this.auction = auction;
    }
}

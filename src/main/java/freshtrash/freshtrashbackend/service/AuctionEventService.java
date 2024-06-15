package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.alarm.CompleteBidAuctionAlarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionEventService {
    private final AuctionService auctionService;
    private final CompleteBidAuctionAlarm completeBidAuctionAlarm;

    @Scheduled(cron = "0 0 0 * * *")
    public void completeAuction() {
        List<Auction> auctions = auctionService.getEndedAuctions();
        // 입찰자 여부를 확인하고 입찰자가 없으면 구매자에게 알림, 있으면 판매자에게 알림
        auctions.forEach(completeBidAuctionAlarm::sendAlarm);
    }
}

package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.service.alarm.CancelAuctionAlarm;
import freshtrash.freshtrashbackend.service.alarm.CompleteBidAuctionAlarm;
import freshtrash.freshtrashbackend.service.alarm.NotPaidAuctionAlarm;
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
    private final BiddingHistoryService biddingHistoryService;
    private final CompleteBidAuctionAlarm completeBidAuctionAlarm;
    private final CancelAuctionAlarm cancelAuctionAlarm;
    private final NotPaidAuctionAlarm notPaidAuctionAlarm;

    @Scheduled(cron = "0 0 0 * * *")
    public void processCompletedAuctions() {
        List<Auction> auctions = auctionService.getEndedAuctions();
        // 입찰자 여부를 확인하고 입찰자가 없으면 구매자에게 알림, 있으면 판매자에게 알림
        auctions.forEach(completeBidAuctionAlarm::sendAlarm);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processNotPaidAuctions() {
        // 낙찰되었지만 24시간 지난 입찰 내역을 조회 후 로직 수행
        biddingHistoryService.getSuccessBiddingHistories().forEach(notPaidAuctionAlarm::sendAlarm);
    }

    public void cancelAuction(Long auctionId, UserRole userRole, Long memberId) {
        auctionService.checkIfWriterOrAdmin(auctionId, userRole, memberId);
        cancelAuctionAlarm.sendAlarm(auctionService.getAuctionWithBiddingHistory(auctionId));
    }
}

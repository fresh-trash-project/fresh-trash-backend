package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.exception.BiddingHistoryException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.BiddingHistoryRepository;
import freshtrash.freshtrashbackend.service.alarm.CompletePayAuctionAlarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingHistoryService {
    private final BiddingHistoryRepository biddingHistoryRepository;
    private final CompletePayAuctionAlarm completePayAuctionAlarm;

    public void addBiddingHistory(Long auctionId, Long memberId, int price) {
        biddingHistoryRepository.save(BiddingHistory.builder()
                .auctionId(auctionId)
                .memberId(memberId)
                .price(price)
                .build());
    }

    /**
     * 낙찰되었지만 결제되지않은 입찰 내역 조회
     */
    public List<BiddingHistory> getSuccessBiddingHistories() {
        return biddingHistoryRepository.findAllNotPaidAnd24HoursAgo(
                LocalDateTime.now().minusDays(1));
    }

    @Transactional
    public void updateToCompletedPayAndNotify(Long auctionId, Long memberId) {
        log.debug("auctionId {} 경매에 입찰한 memberId {} 유저가 입찰한 내역 중 가장 큰 금액으로 입찰한 내역 조회", auctionId, memberId);
        BiddingHistory biddingHistory = getWinningBiddingHistoryByAuctionIdAndMemberId(auctionId, memberId);
        completePayAuctionAlarm.sendAlarm(biddingHistory);
    }

    public void updateSuccessBidAt(Long auctionId) {
        biddingHistoryRepository.updateSuccessBidAtByAuctionId(auctionId);
    }

    public BiddingHistory getWinningBiddingHistoryByAuctionIdAndMemberId(Long auctionId, Long memberId) {
        return biddingHistoryRepository
                .findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId)
                .orElseThrow(() -> new BiddingHistoryException(ErrorCode.NOT_FOUND_BIDDING_HISTORY));
    }

    public Page<BiddingHistory> getWinningBiddingHistoriesByMemberId(Long memberId, Pageable pageable) {
        return biddingHistoryRepository.findAllByMemberIdAndSuccessBidAtNotNull(memberId, pageable);
    }

    public void deleteBiddingHistory(Long biddingHistoryId) {
        biddingHistoryRepository.deleteById(biddingHistoryId);
    }
}

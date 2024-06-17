package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.exception.BiddingHistoryException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.BiddingHistoryRepository;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingHistoryService {
    private final BiddingHistoryRepository biddingHistoryRepository;
    private final AuctionPublisher auctionPublisher;

    public void addBiddingHistory(Long auctionId, Long memberId, int price) {
        biddingHistoryRepository.save(BiddingHistory.builder()
                .auctionId(auctionId)
                .memberId(memberId)
                .price(price)
                .build());
    }

    @Transactional
    public void updateToCompletedPayAndNotify(Long auctionId, Long memberId) {
        log.debug("auctionId {} 경매에 입찰한 memberId {} 유저가 입찰한 내역 중 가장 큰 금액으로 입찰한 내역 조회", auctionId, memberId);
        BiddingHistory biddingHistory = getBiddingHistoryByAuctionIdAndMemberId(auctionId, memberId);
        biddingHistory.setPay(true);
        log.debug("결제한 유저와 판매자에게 결제 완료 알림 전송");
        auctionPublisher.publishForCompletedPayAndRequestDelivery(biddingHistory);
    }

    private BiddingHistory getBiddingHistoryByAuctionIdAndMemberId(Long auctionId, Long memberId) {
        return biddingHistoryRepository
                .findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId)
                .orElseThrow(() -> new BiddingHistoryException(ErrorCode.NOT_FOUND_BIDDING_HISTORY));
    }
}

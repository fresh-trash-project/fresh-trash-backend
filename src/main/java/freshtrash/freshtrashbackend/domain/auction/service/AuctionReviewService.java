package freshtrash.freshtrashbackend.domain.auction.service;

import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.entity.AuctionReview;
import freshtrash.freshtrashbackend.global.exception.ReviewException;
import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.domain.auction.repository.AuctionReviewRepository;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionReviewService {
    private final AuctionReviewRepository auctionReviewRepository;
    private final AuctionService auctionService;
    private final AuctionProducer auctionProducer;

    @Transactional
    public AuctionReview insertAuctionReview(AuctionReviewRequest auctionReviewRequest, Long auctionId, Long memberId) {
        // 이미 리뷰가 등록되어있는지 확인
        if (auctionReviewRepository.existsByAuctionId(auctionId)) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }
        log.debug("경매 리뷰 저장");
        AuctionReview auctionReview = AuctionReview.fromRequest(auctionReviewRequest, auctionId, memberId);
        auctionReview = auctionReviewRepository.save(auctionReview);

        log.debug("판매자에게 리뷰 알림 전송");
        Auction auction = auctionService.getAuction(auctionId);
        auctionProducer.publishToSellerForReview(auction, memberId);
        return auctionReview;
    }
}

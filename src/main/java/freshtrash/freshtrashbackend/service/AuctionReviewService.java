package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.AuctionReview;
import freshtrash.freshtrashbackend.exception.ReviewException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.AuctionReviewRepository;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
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
    private final AuctionPublisher auctionPublisher;

    @Transactional
    public AuctionReview insertAuctionReview(ReviewRequest reviewRequest, Long auctionId, Long memberId) {
        // 이미 리뷰가 등록되어있는지 확인
        if (auctionReviewRepository.existsByAuctionId(auctionId)) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }
        log.debug("경매 리뷰 저장");
        AuctionReview auctionReview = AuctionReview.fromRequest(reviewRequest, auctionId, memberId);
        auctionReview = auctionReviewRepository.save(auctionReview);

        log.debug("판매자에게 리뷰 알림 전송");
        Auction auction = auctionService.getAuction(auctionId);
        auctionPublisher.publishToSellerForReview(auction, memberId);
        return auctionReview;
    }
}

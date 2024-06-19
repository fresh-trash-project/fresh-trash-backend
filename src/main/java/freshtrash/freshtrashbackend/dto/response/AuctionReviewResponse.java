package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.AuctionReview;
import lombok.Builder;

@Builder
public record AuctionReviewResponse(Long memberId, Long auctionId, Integer rating, String content) {

    public static AuctionReviewResponse fromEntity(AuctionReview auctionReview) {
        return AuctionReviewResponse.builder()
                .memberId(auctionReview.getMemberId())
                .auctionId(auctionReview.getAuctionId())
                .rating(auctionReview.getRating())
                .content(auctionReview.getContent())
                .build();
    }
}

package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.AuctionReview;
import freshtrash.freshtrashbackend.entity.ProductReview;
import lombok.Builder;

@Builder
public record ReviewResponse(Long memberId, Long productId, Integer rating, String content) {
    public static ReviewResponse fromEntity(ProductReview productReview) {
        return ReviewResponse.builder()
                .memberId(productReview.getMemberId())
                .productId(productReview.getProductId())
                .rating(productReview.getRating())
                .build();
    }

    public static ReviewResponse fromEntity(AuctionReview auctionReview) {
        return ReviewResponse.builder()
                .memberId(auctionReview.getMemberId())
                .productId(auctionReview.getAuctionId())
                .rating(auctionReview.getRating())
                .content(auctionReview.getContent())
                .build();
    }
}

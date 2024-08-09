package freshtrash.freshtrashbackend.domain.product.dto.response;

import freshtrash.freshtrashbackend.domain.product.entity.ProductReview;
import lombok.Builder;

@Builder
public record ProductReviewResponse(Long memberId, Long productId, Integer rating, String content) {
    public static ProductReviewResponse fromEntity(ProductReview productReview) {
        return ProductReviewResponse.builder()
                .memberId(productReview.getMemberId())
                .productId(productReview.getProductId())
                .rating(productReview.getRating())
                .build();
    }
}

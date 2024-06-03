package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.ProductReview;
import lombok.Builder;

@Builder
public record ReviewResponse(Long memberId, Long productId, Integer rating) {
    public static ReviewResponse fromEntity(ProductReview productReview) {
        return ReviewResponse.builder()
                .memberId(productReview.getMemberId())
                .productId(productReview.getProductId())
                .rating(productReview.getRating())
                .build();
    }
}

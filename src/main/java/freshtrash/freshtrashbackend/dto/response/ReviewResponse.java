package freshtrash.freshtrashbackend.dto.response;

import freshtrash.freshtrashbackend.entity.WasteReview;
import lombok.Builder;

@Builder
public record ReviewResponse(Long memberId, Long wasteId, Integer rating) {
    public static ReviewResponse fromEntity(WasteReview wasteReview) {
        return ReviewResponse.builder()
                .memberId(wasteReview.getMemberId())
                .wasteId(wasteReview.getWasteId())
                .rating(wasteReview.getRating())
                .build();
    }
}

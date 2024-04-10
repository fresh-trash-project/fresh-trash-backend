package freshtrash.freshtrashbackend.dto;

import freshtrash.freshtrashbackend.entity.WasteReview;
import lombok.Builder;

@Builder
public record WasteReviewDto(Long memberId, Long wasteId, Integer rating) {
    public static WasteReviewDto fromEntity(WasteReview wasteReview) {
        return WasteReviewDto.builder()
                .memberId(wasteReview.getMemberId())
                .wasteId(wasteReview.getWasteId())
                .rating(wasteReview.getRating())
                .build();
    }
}

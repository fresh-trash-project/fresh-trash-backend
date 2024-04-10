package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.WasteReview;

import javax.validation.constraints.NotNull;

public record ReviewRequest(@NotNull Integer rate, String content) {

    public WasteReview toEntity(Long wasteId, Long memberId) {
        return WasteReview.builder()
                .rating(rate)
                .memberId(memberId)
                .wasteId(wasteId)
                .review(content)
                .build();
    }
}

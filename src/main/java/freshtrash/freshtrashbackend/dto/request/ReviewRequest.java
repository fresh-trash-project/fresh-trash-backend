package freshtrash.freshtrashbackend.dto.request;

import freshtrash.freshtrashbackend.entity.WasteReview;

import javax.validation.constraints.NotNull;

public record ReviewRequest(@NotNull Integer rate) {

    public WasteReview toEntity(Long wasteId, Long memberId) {
        return WasteReview.builder()
                .rating(rate)
                .memberId(memberId)
                .wasteId(wasteId)
                .build();
    }
}

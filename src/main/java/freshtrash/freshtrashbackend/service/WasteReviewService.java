package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.WasteReview;
import freshtrash.freshtrashbackend.exception.ReviewException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.WasteReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteReviewService {
    private final WasteReviewRepository wasteReviewRepository;

    public WasteReview insertWasteReview(ReviewRequest reviewRequest, Long wasteId, Long memberId) {
        // 이미 리뷰가 등록되있는지 확인
        if (wasteReviewRepository.existsByWasteId(wasteId)) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }
        WasteReview wasteReview = WasteReview.fromRequest(reviewRequest, wasteId, memberId);
        return wasteReviewRepository.save(wasteReview);
    }
}

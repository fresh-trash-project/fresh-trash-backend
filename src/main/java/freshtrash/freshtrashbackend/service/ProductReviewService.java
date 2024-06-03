package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.ProductReview;
import freshtrash.freshtrashbackend.exception.ReviewException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    public ProductReview insertProductReview(ReviewRequest reviewRequest, Long productId, Long memberId) {
        // 이미 리뷰가 등록되있는지 확인
        if (productReviewRepository.existsByProductId(productId)) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }
        ProductReview productReview = ProductReview.fromRequest(reviewRequest, productId, memberId);
        return productReviewRepository.save(productReview);
    }
}

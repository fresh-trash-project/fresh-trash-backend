package freshtrash.freshtrashbackend.domain.product.service;

import freshtrash.freshtrashbackend.domain.product.dto.request.ProductReviewRequest;
import freshtrash.freshtrashbackend.domain.product.entity.ProductReview;
import freshtrash.freshtrashbackend.global.exception.ReviewException;
import freshtrash.freshtrashbackend.global.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.domain.product.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    public ProductReview insertProductReview(ProductReviewRequest productReviewRequest, Long productId, Long memberId) {
        // 이미 리뷰가 등록되있는지 확인
        if (productReviewRepository.existsByProductId(productId)) {
            throw new ReviewException(ErrorCode.ALREADY_EXISTS_REVIEW);
        }
        ProductReview productReview = ProductReview.fromRequest(productReviewRequest, productId, memberId);
        return productReviewRepository.save(productReview);
    }
}

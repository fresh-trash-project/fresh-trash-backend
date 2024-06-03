package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.ProductReview;
import freshtrash.freshtrashbackend.repository.ProductReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceTest {
    @InjectMocks
    private ProductReviewService productReviewService;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Test
    @DisplayName("리뷰(평점) 작성")
    void given_reviewRequestAndProductIdAndMemberId_when_insertProductReview_then_returnProductReview() {
        // given
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(4);
        Long productId = 1L;
        Long memberId = 123L;
        ProductReview productReview = ProductReview.fromRequest(reviewRequest, productId, memberId);
        given(productReviewRepository.save(any(ProductReview.class))).willReturn(productReview);
        // when
        ProductReview result = productReviewService.insertProductReview(reviewRequest, productId, memberId);
        // then
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getRating()).isEqualTo(reviewRequest.rate());
    }
}
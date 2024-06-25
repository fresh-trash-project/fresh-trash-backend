package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.ProductReview;
import freshtrash.freshtrashbackend.exception.ReviewException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.ProductReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
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
        given(productReviewRepository.existsByProductId(productId)).willReturn(false);
        given(productReviewRepository.save(any(ProductReview.class))).willReturn(productReview);
        // when
        ProductReview result = productReviewService.insertProductReview(reviewRequest, productId, memberId);
        // then
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getRating()).isEqualTo(reviewRequest.rate());
    }

    @Test
    @DisplayName("ReviewRequest와 productId를 입력받고 이미 리뷰가 등록되어있다면 예외를 발생시킨다.")
    void given_reviewRequestAndProductIdAndMemberId_when_existsReview_then_throwException() {
        // given
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(4);
        Long productId = 1L;
        Long memberId = 123L;
        given(productReviewRepository.existsByProductId(productId)).willReturn(true);
        // when
        assertThatThrownBy(() -> productReviewService.insertProductReview(reviewRequest, productId, memberId))
                .isInstanceOf(ReviewException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EXISTS_REVIEW);
        // then
    }
}
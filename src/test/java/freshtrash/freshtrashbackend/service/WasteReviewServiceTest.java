package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.WasteReview;
import freshtrash.freshtrashbackend.repository.WasteReviewRepository;
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
class WasteReviewServiceTest {
    @InjectMocks
    private WasteReviewService wasteReviewService;

    @Mock
    private WasteReviewRepository wasteReviewRepository;

    @Test
    @DisplayName("리뷰(평점) 작성")
    void given_reviewRequestAndWasteIdAndMemberId_when_insertWasteReview_then_returnWasteReview() {
        // given
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(4);
        Long wasteId = 1L;
        Long memberId = 123L;
        WasteReview wasteReview = WasteReview.fromRequest(reviewRequest, wasteId, memberId);
        given(wasteReviewRepository.save(any(WasteReview.class))).willReturn(wasteReview);
        // when
        WasteReview result = wasteReviewService.insertWasteReview(reviewRequest, wasteId, memberId);
        // then
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getWasteId()).isEqualTo(wasteId);
        assertThat(result.getRating()).isEqualTo(reviewRequest.rate());
    }
}
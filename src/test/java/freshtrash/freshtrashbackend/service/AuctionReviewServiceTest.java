package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ReviewRequest;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.AuctionReview;
import freshtrash.freshtrashbackend.repository.AuctionReviewRepository;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuctionReviewServiceTest {
    @InjectMocks
    private AuctionReviewService auctionReviewService;

    @Mock
    private AuctionReviewRepository auctionReviewRepository;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuctionProducer auctionProducer;

    @Test
    @DisplayName("이미 리뷰가 등록되어있는지 확인 후 저장하고 판매자에게 알림을 전송한다.")
    void given_reviewRequestAndAuctionIdAndMemberId_when_notWroteReview_then_insertReviewAndNotify() {
        // given
        Long auctionId = 2L, memberId = 123L;
        ReviewRequest reviewRequest = FixtureDto.createReviewRequest(3, "content");
        AuctionReview auctionReview = AuctionReview.fromRequest(reviewRequest, auctionId, memberId);
        Auction auction = Fixture.createAuction();
        ReflectionTestUtils.setField(auctionReview, "auction", auction);
        given(auctionReviewRepository.existsByAuctionId(auctionId)).willReturn(false);
        given(auctionReviewRepository.save(any(AuctionReview.class))).willReturn(auctionReview);
        given(auctionService.getAuction(auctionId)).willReturn(auction);
        willDoNothing().given(auctionProducer).publishToSellerForReview(auctionReview.getAuction(), memberId);
        // when
        AuctionReview savedAuctionReview = auctionReviewService.insertAuctionReview(reviewRequest, auctionId, memberId);
        // then
        assertThat(savedAuctionReview.getContent()).isEqualTo(reviewRequest.content());
        assertThat(savedAuctionReview.getRating()).isEqualTo(reviewRequest.rate());
    }
}
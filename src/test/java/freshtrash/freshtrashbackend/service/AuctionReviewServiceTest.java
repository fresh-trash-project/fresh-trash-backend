package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.domain.auction.dto.request.AuctionReviewRequest;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.entity.AuctionReview;
import freshtrash.freshtrashbackend.domain.auction.repository.AuctionReviewRepository;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionReviewService;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
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
        AuctionReviewRequest auctionReviewRequest = FixtureDto.createAuctionReviewRequest(3, "content");
        AuctionReview auctionReview = AuctionReview.fromRequest(auctionReviewRequest, auctionId, memberId);
        Auction auction = Fixture.createAuction();
        ReflectionTestUtils.setField(auctionReview, "auction", auction);
        given(auctionReviewRepository.existsByAuctionId(auctionId)).willReturn(false);
        given(auctionReviewRepository.save(any(AuctionReview.class))).willReturn(auctionReview);
        given(auctionService.getAuction(auctionId)).willReturn(auction);
        willDoNothing().given(auctionProducer).publishToSellerForReview(auctionReview.getAuction(), memberId);
        // when
        AuctionReview savedAuctionReview =
                auctionReviewService.insertAuctionReview(auctionReviewRequest, auctionId, memberId);
        // then
        assertThat(savedAuctionReview.getContent()).isEqualTo(auctionReviewRequest.content());
        assertThat(savedAuctionReview.getRating()).isEqualTo(auctionReviewRequest.rate());
    }
}
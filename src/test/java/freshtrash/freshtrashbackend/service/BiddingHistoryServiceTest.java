package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.repository.BiddingHistoryRepository;
import freshtrash.freshtrashbackend.service.alarm.CompletePayAuctionAlarm;
import freshtrash.freshtrashbackend.service.producer.AuctionPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BiddingHistoryServiceTest {

    @InjectMocks
    private BiddingHistoryService biddingHistoryService;

    @Mock
    private BiddingHistoryRepository biddingHistoryRepository;

    @Mock
    private CompletePayAuctionAlarm completePayAuctionAlarm;

    @Test
    @DisplayName("결제 완료 후 낙찰된 입찰 내역의 결제 여부를 TRUE로 업데이트하고 판매자/구매자에게 알림 전송")
    void given_auctionIdAndMemberId_when_completedPay_then_updateIsPayAndNotify() {
        // given
        Long auctionId = 2L, memberId = 1L;
        BiddingHistory biddingHistory = Fixture.createBiddingHistory(auctionId, memberId, 1000);
        given(biddingHistoryRepository.findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId))
                .willReturn(Optional.of(biddingHistory));
        willDoNothing().given(completePayAuctionAlarm).sendAlarm(biddingHistory);
        // when
        biddingHistoryService.updateToCompletedPayAndNotify(auctionId, memberId);
        // then
        then(biddingHistoryRepository).should().findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId);
        then(completePayAuctionAlarm).should().sendAlarm(biddingHistory);
    }
}
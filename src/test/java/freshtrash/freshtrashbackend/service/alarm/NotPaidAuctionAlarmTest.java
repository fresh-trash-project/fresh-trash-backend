package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.projections.FlagCountSummary;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.BiddingHistoryService;
import freshtrash.freshtrashbackend.service.MemberService;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NotPaidAuctionAlarmTest {
    @InjectMocks
    private NotPaidAuctionAlarm notPaidAuctionAlarm;

    @Mock
    private MemberService memberService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private BiddingHistoryService biddingHistoryService;

    @Mock
    private AuctionProducer producer;

    @Test
    @DisplayName("낙찰된 경매가 결제되지 않았을 경우 해당 경매를 취소하고 입찰 내역을 삭제한 후 알림을 전송한다.")
    void given_biddingHistory_when_notPaid_then_cancelAuctionAndDeleteBiddingHistoryANdSendAlarm() {
        // given
        BiddingHistory biddingHistory = Fixture.createBiddingHistory(1L, 2L, 1000);
        given(memberService.updateFlagCount(biddingHistory.getMemberId(), Member.USER_FLAG_LIMIT))
                .willReturn(new FlagCountSummary(3));
        willDoNothing().given(auctionService).cancelAuction(biddingHistory.getAuctionId());
        willDoNothing().given(biddingHistoryService).deleteBiddingHistory(biddingHistory.getId());
        willDoNothing().given(producer).publishForNotPaid(biddingHistory);
        // when
        assertThatCode(() -> notPaidAuctionAlarm.sendAlarm(biddingHistory)).doesNotThrowAnyException();
        // then
    }
}
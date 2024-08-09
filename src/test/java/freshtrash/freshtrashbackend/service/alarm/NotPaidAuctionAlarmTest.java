package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.alarm.service.NotPaidAuctionAlarm;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.BiddingHistoryAlarmParameter;
import freshtrash.freshtrashbackend.domain.member.dto.projections.FlagCountSummary;
import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.domain.auction.service.BiddingHistoryService;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
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
        BiddingHistoryAlarmParameter biddingHistoryAlarmParameter = new BiddingHistoryAlarmParameter(biddingHistory);
        given(memberService.updateFlagCount(biddingHistory.getMemberId(), Member.USER_FLAG_LIMIT))
                .willReturn(new FlagCountSummary(3));
        willDoNothing().given(auctionService).cancelAuction(biddingHistory.getAuctionId());
        willDoNothing().given(biddingHistoryService).deleteBiddingHistory(biddingHistory.getId());
        willDoNothing().given(producer).publishForNotPaid(biddingHistory);
        // when
        assertThatCode(() -> notPaidAuctionAlarm.sendAlarm(biddingHistoryAlarmParameter))
                .doesNotThrowAnyException();
        // then
    }

    @DisplayName("NOT_PAY 타입의 알람 전송을 수행하는 작업을 지원한다.")
    @Test
    void given_alarmType_when_supported_then_returnTrue() {
        //given
        AlarmType alarmType = AlarmType.NOT_PAY;
        //when
        boolean isSupport = notPaidAuctionAlarm.supports(alarmType);
        //then
        assertThat(isSupport).isTrue();
    }
}
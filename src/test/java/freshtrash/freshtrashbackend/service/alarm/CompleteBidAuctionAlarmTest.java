package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.CompleteBidAuctionAlarm;
import freshtrash.freshtrashbackend.domain.auction.entity.Auction;
import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.domain.auction.service.BiddingHistoryService;
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
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CompleteBidAuctionAlarmTest {
    @InjectMocks
    private CompleteBidAuctionAlarm completeBidAuctionAlarm;

    @Mock
    private BiddingHistoryService biddingHistoryService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuctionProducer producer;

    @Test
    @DisplayName("낙찰되었을 경우 경매를 닫고 가장 금액이 큰 입찰 내역을 낙찰 처리한 후 판매자, 구매자에게 알림을 전송한다.")
    void given_auction_when_successBid_then_closeAuctionAndUpdateBidAtAndSendAlarmToSellerAndBuyer() {
        // given
        Auction auction = Fixture.createAuction();
        BiddingHistory biddingHistory =
                auction.getBiddingHistories().stream().findFirst().get();
        willDoNothing().given(auctionService).closeAuction(auction.getId());
        willDoNothing().given(biddingHistoryService).updateSuccessBidAt(auction.getId());
        willDoNothing().given(producer).publishToSellerForCompletedAuction(auction, biddingHistory.getMemberId());
        willDoNothing().given(producer).publishToWonBidderForRequestPay(auction, biddingHistory.getMemberId());
        // when
        assertThatCode(() -> completeBidAuctionAlarm.sendAlarm(auction)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("낙찰되었을 경우 경매를 닫고 입찰 내역이 없는 경우 판매자에게만 알림을 전송한다.")
    void given_auction_when_successBidAndNotExistsBidder_then_closeAuctionAndSendAlarmToSeller() {
        // given
        Auction auction = Fixture.createAuction();
        auction.getBiddingHistories().clear();
        willDoNothing().given(auctionService).closeAuction(auction.getId());
        willDoNothing().given(biddingHistoryService).updateSuccessBidAt(auction.getId());
        willDoNothing().given(producer).publishToSellerForNotExistBidders(auction);
        // when
        assertThatCode(() -> completeBidAuctionAlarm.sendAlarm(auction)).doesNotThrowAnyException();
        // then
    }

    @DisplayName("BIDDING 타입의 알람 전송을 수행하는 작업을 지원한다.")
    @Test
    void given_alarmType_when_supported_then_returnTrue() {
        // given
        AlarmType alarmType = AlarmType.BIDDING;
        // when
        boolean isSupport = completeBidAuctionAlarm.supports(alarmType);
        // then
        assertThat(isSupport).isTrue();
    }
}
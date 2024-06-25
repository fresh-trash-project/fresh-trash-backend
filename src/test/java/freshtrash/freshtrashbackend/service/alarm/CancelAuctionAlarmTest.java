package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.AuctionService;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CancelAuctionAlarmTest {
    @InjectMocks
    private CancelAuctionAlarm cancelAuctionAlarm;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuctionProducer producer;

    @Test
    @DisplayName("경매의 상태를 취소로 처리하고 입찰자, 구매자에게 알린다.")
    void given_auction_when_cancelAuction_then_sendAlarmToSellerAndBuyer() {
        // given
        Auction auction = Fixture.createAuction();
        BiddingHistory biddingHistory =
                auction.getBiddingHistories().stream().findFirst().get();
        willDoNothing().given(auctionService).deleteAuction(auction.getId());
        willDoNothing().given(producer).publishToBiddersForCancelAuction(auction, biddingHistory.getMemberId());
        willDoNothing().given(producer).publishToSellerForCancelAuction(auction);
        // when
        assertThatCode(() -> cancelAuctionAlarm.sendAlarm(auction)).doesNotThrowAnyException();
        // then
    }
}
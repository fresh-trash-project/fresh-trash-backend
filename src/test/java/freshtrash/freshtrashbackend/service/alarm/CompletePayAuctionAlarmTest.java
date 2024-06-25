package freshtrash.freshtrashbackend.service.alarm;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.producer.AuctionProducer;
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
class CompletePayAuctionAlarmTest {
    @InjectMocks
    private CompletePayAuctionAlarm completePayAuctionAlarm;

    @Mock
    private AuctionProducer producer;

    @Test
    @DisplayName("낙찰된 경매 상품을 결제 완료하면 해당 입찰 내역의 결제 여부를 true로 변경하고 알림을 전송한다.")
    void given_biddingHistory_when_completePay_then_updatePayAndSendAlarm() {
        // given
        Long auctionId = 1L, memberId = 2L;
        int price = 1000;
        BiddingHistory biddingHistory = Fixture.createBiddingHistory(auctionId, memberId, price);
        willDoNothing().given(producer).publishForCompletedPayAndRequestDelivery(biddingHistory);
        // when
        assertThatCode(() -> completePayAuctionAlarm.sendAlarm(biddingHistory)).doesNotThrowAnyException();
        // then
        assertThat(biddingHistory.isPay()).isTrue();
    }
}
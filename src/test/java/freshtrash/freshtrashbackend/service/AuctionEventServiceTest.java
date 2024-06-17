package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.service.alarm.CancelAuctionAlarm;
import freshtrash.freshtrashbackend.service.alarm.CompleteBidAuctionAlarm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuctionEventServiceTest {
    @InjectMocks
    private AuctionEventService auctionEventService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private CompleteBidAuctionAlarm completeBidAuctionAlarm;

    @Mock
    private CancelAuctionAlarm cancelAuctionAlarm;

    @DisplayName("매일 0시에 마감된 경매를 조회하고 낙찰자에게 알림을 전송합니다.")
    @Test
    void given_endedAuctions_when_existsBidUser_then_closeAuctionAndSendAlarm() {
        // given
        Auction auction = Fixture.createAuction();
        given(auctionService.getEndedAuctions()).willReturn(List.of(auction));
        willDoNothing().given(completeBidAuctionAlarm).sendAlarm(auction);
        // when
        auctionEventService.processCompletedAuctions();
        // then
        then(auctionService).should().getEndedAuctions();
        then(completeBidAuctionAlarm).should(times(1)).sendAlarm(auction);
    }

    @DisplayName("경매가 취소되면 입찰자들에게 알림을 전송합니다.")
    @Test
    void given_auctionIdAndLoginUser_when_writerOrAdmin_then_deleteAuctionAndNotifyToBidUsers() {
        // given
        Long auctionId = 1L, memberId = 2L;
        UserRole userRole = UserRole.USER;
        Auction auction = Fixture.createAuction();
        willDoNothing().given(auctionService).checkIfWriterOrAdmin(auctionId, userRole, memberId);
        given(auctionService.getAuctionWithBiddingHistory(auctionId)).willReturn(auction);
        willDoNothing().given(cancelAuctionAlarm).sendAlarm(auction);
        // when
        auctionEventService.cancelAuction(auctionId, userRole, memberId);
        // then
    }
}
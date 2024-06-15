package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.alarm.CompleteBidAuctionAlarm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.then;
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

    @DisplayName("경매 낙찰")
    @Test
    void given_endedAuctions_when_existsBidUser_then_closeAuctionAndSendAlarm() {
        // given
        Auction auction = Fixture.createAuction();
        given(auctionService.getEndedAuctions()).willReturn(List.of(auction));
        willDoNothing().given(completeBidAuctionAlarm).sendAlarm(auction);
        // when
        auctionEventService.completeAuction();
        // then
        then(auctionService).should().getEndedAuctions();
        then(completeBidAuctionAlarm).should(times(1)).sendAlarm(auction);
    }
}
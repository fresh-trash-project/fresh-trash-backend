package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.repository.BiddingHistoryRepository;
import freshtrash.freshtrashbackend.service.alarm.CompletePayAuctionAlarm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
    @DisplayName("auctionId, memberId, 입찰가격(price)를 입력받아 BiddingHistory를 생성하고 저장한다.")
    void given_auctionIdAndMemberIdAndPrice_when_constructBiddingHistory_then_saveBiddingHistory() {
        // given
        Long auctionId = 1L, memberId = 3L;
        int price = 1000;
        BiddingHistory biddingHistory = Fixture.createBiddingHistoryWithAuctionAndMember(auctionId, memberId, price);
        given(biddingHistoryRepository.save(biddingHistory)).willReturn(biddingHistory);
        // when
        assertThatCode(() -> biddingHistoryService.addBiddingHistory(auctionId, memberId, price))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("낙찰되었지만 24시간 이내에 결제되지 않은 입찰내역을 모두 조회한다.")
    void should_findBiddingHistories_when_SuccessBidAndNotPaid() {
        // given
        LocalDateTime now = LocalDateTime.now();
        try (MockedStatic<LocalDateTime> dateTimeMockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            dateTimeMockedStatic.when(LocalDateTime::now).thenReturn(now);
            given(biddingHistoryRepository.findAllNotPaidAnd24HoursAgo(now.minusDays(1)))
                    .willReturn(List.of(Fixture.createBiddingHistory(1L, 2L, 1000)));
            // when
            biddingHistoryService.getSuccessBiddingHistories();
        }
        // then
    }

    @Test
    @DisplayName("결제 완료 후 낙찰된 입찰 내역의 결제 여부를 TRUE로 업데이트하고 판매자/구매자에게 알림 전송")
    void given_auctionIdAndMemberId_when_completedPay_then_updateIsPayAndNotify() {
        // given
        Long auctionId = 2L, memberId = 1L;
        BiddingHistory biddingHistory = Fixture.createBiddingHistoryWithAuctionAndMember(auctionId, memberId, 1000);
        given(biddingHistoryRepository.findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId))
                .willReturn(Optional.of(biddingHistory));
        willDoNothing().given(completePayAuctionAlarm).sendAlarm(biddingHistory);
        // when
        biddingHistoryService.updateToCompletedPayAndNotify(auctionId, memberId);
        // then
        then(biddingHistoryRepository).should().findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId);
        then(completePayAuctionAlarm).should().sendAlarm(biddingHistory);
    }

    @Test
    @DisplayName("auctionId를 입력받아 SuccessBidAt을 업데이트한다.")
    void given_auctionId_when_then_updateSuccessBidAt() {
        // given
        Long auctionId = 1L;
        willDoNothing().given(biddingHistoryRepository).updateSuccessBidAtByAuctionId(auctionId);
        // when
        assertThatCode(() -> biddingHistoryService.updateSuccessBidAt(auctionId))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("auctionId와 memberId를 받아 가장 입찰 금액이 큰 입찰 내역을 조회한다.")
    void given_auctionIdAndMemberId_when_largestPrice_then_returnBiddingHistory() {
        // given
        Long auctionId = 1L, memberId = 2L;
        BiddingHistory biddingHistory = Fixture.createBiddingHistoryWithAuctionAndMember(auctionId, memberId, 10000);
        given(biddingHistoryRepository.findFirstByAuctionIdAndMemberIdOrderByPriceDesc(auctionId, memberId))
                .willReturn(Optional.of(biddingHistory));
        // when
        BiddingHistory foundBiddingHistory =
                biddingHistoryService.getWinningBiddingHistoryByAuctionIdAndMemberId(auctionId, memberId);
        // then
        assertThat(foundBiddingHistory).isNotNull();
    }

    @Test
    @DisplayName("입찰 내역의 id를 받아 삭제한다.")
    void given_biddingHistoryId_when_then_delete() {
        // given
        Long biddingHistoryId = 1L;
        willDoNothing().given(biddingHistoryRepository).deleteById(biddingHistoryId);
        // when
        assertThatCode(() -> biddingHistoryService.deleteBiddingHistory(biddingHistoryId))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("memberId를 입력받아 낙찰된 입찰 내역을 모두 조회한다.")
    void given_memberId_when_winningBid_then_returnBiddingHistories() {
        // given
        Long memberId = 12L, auctionId = 1L;
        Pageable pageable = PageRequest.of(0, 6);
        given(biddingHistoryRepository.findAllByMemberIdAndSuccessBidAtNotNull(memberId, pageable))
                .willReturn(new PageImpl<>(List.of(Fixture.createBiddingHistory(auctionId, memberId, 10000))));
        // when
        Page<BiddingHistory> biddingHistories =
                biddingHistoryService.getWinningBiddingHistoriesByMemberId(memberId, pageable);
        // then
        assertThat(biddingHistories.getTotalElements()).isEqualTo(1);
    }
}
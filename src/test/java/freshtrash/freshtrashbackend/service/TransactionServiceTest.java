package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.TransactionLog;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.TransactionLogRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionLogRepository transactionLogRepository;

    @Mock
    private WasteRepository wasteRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @DisplayName("거래 내역 저장")
    @Test
    void given_wasteAndChatRoomAndSellerAndBuyerAndSellStatus_when_then_updateSellStatusAndSaveLog() {
        // given
        Long wasteId = 1L;
        Long chatRoomId = 3L;
        Long sellerId = 1L;
        Long buyerId = 2L;
        SellStatus sellStatus = SellStatus.CLOSE;
        given(transactionLogRepository.save(any(TransactionLog.class)))
                .willReturn(Fixture.createTransactionLog(wasteId, sellerId, buyerId));
        willDoNothing().given(wasteRepository).updateSellStatus(anyLong(), any(SellStatus.class));
        willDoNothing().given(chatRoomRepository).updateSellStatus(anyLong(), any(SellStatus.class));
        // when
        transactionService.completeTransaction(wasteId, chatRoomId, sellerId, buyerId, sellStatus);
        ArgumentCaptor<TransactionLog> captor = ArgumentCaptor.forClass(TransactionLog.class);
        // then
        verify(transactionLogRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getWasteId()).isEqualTo(wasteId);
        assertThat(captor.getValue().getSellerId()).isEqualTo(sellerId);
        assertThat(captor.getValue().getBuyerId()).isEqualTo(buyerId);
    }
}

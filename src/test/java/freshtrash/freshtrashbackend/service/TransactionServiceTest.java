package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import freshtrash.freshtrashbackend.repository.TransactionLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionLogRepository transactionLogRepository;

    @DisplayName("거래 내역 저장")
    @Test
    void given_wasteIdAndSellerIdAndBuyerId_when_then_saveTransactionLog() {
        // given
        Long wasteId = 1L;
        Long sellerId = 1L;
        Long buyerId = 2L;
        // when
        transactionService.saveTransactionLog(wasteId, sellerId, buyerId);
        ArgumentCaptor<TransactionLog> captor = ArgumentCaptor.forClass(TransactionLog.class);
        // then
        verify(transactionLogRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getWasteId()).isEqualTo(wasteId);
        assertThat(captor.getValue().getSellerId()).isEqualTo(sellerId);
        assertThat(captor.getValue().getBuyerId()).isEqualTo(buyerId);
    }
}

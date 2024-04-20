package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.TransactionLogRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionLogRepository transactionLogRepository;
    private final WasteRepository wasteRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * - 폐기물과 채팅방의 판매 상태 변경
     * - 거래 내역 저장
     */
    @Transactional
    public void completeTransaction(Long wasteId, Long chatRoomId, Long sellerId, Long buyerId, SellStatus sellStatus) {
        updateSellStatus(wasteId, chatRoomId, sellStatus);
        saveTransactionLog(wasteId, sellerId, buyerId);
    }

    private void saveTransactionLog(Long wasteId, Long sellerId, Long buyerId) {
        transactionLogRepository.save(TransactionLog.builder()
                .wasteId(wasteId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build());
    }

    private void updateSellStatus(Long wasteId, Long chatRoomId, SellStatus sellStatus) {
        wasteRepository.updateSellStatus(wasteId, sellStatus);
        chatRoomRepository.updateSellStatus(chatRoomId, sellStatus);
    }
}

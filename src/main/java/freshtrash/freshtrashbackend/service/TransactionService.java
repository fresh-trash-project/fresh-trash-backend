package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.constants.TransactionMemberType;
import freshtrash.freshtrashbackend.dto.response.WasteResponse;
import freshtrash.freshtrashbackend.entity.TransactionLog;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import freshtrash.freshtrashbackend.repository.TransactionLogRepository;
import freshtrash.freshtrashbackend.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionLogRepository transactionLogRepository;
    private final WasteRepository wasteRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Page<WasteResponse> getTransactedWastes(Long memberId, TransactionMemberType memberType, Pageable pageable) {
        switch (memberType) {
                // 판매 완료 wastes
            case SELLER_CLOSE -> {
                return transactionLogRepository
                        .findAllBySeller_Id(memberId, pageable)
                        .map(TransactionLog::getWaste)
                        .map(WasteResponse::fromEntity);
            }
                // 판매 중 또는 예약 중 wastes
            case SELLER_ONGOING -> {
                return wasteRepository
                        .findAllByMemberIdAndSellStatusNot(memberId, SellStatus.CLOSE, pageable)
                        .map(WasteResponse::fromEntity);
            }
                // 구매 wastes
            default -> {
                return transactionLogRepository
                        .findAllByBuyer_Id(memberId, pageable)
                        .map(TransactionLog::getWaste)
                        .map(WasteResponse::fromEntity);
            }
        }
    }

    /**
     * - 폐기물과 채팅방의 판매 상태 변경
     * - 거래 내역 저장
     */
    @Transactional
    public void completeTransaction(Long wasteId, Long chatRoomId, Long sellerId, Long buyerId, SellStatus sellStatus) {
        updateSellStatus(wasteId, chatRoomId, sellStatus);
        saveTransactionLog(wasteId, sellerId, buyerId);
    }

    @Transactional
    public void updateSellStatus(Long wasteId, Long chatRoomId, SellStatus sellStatus) {
        wasteRepository.updateSellStatus(wasteId, sellStatus);
        chatRoomRepository.updateSellStatus(chatRoomId, sellStatus);
    }

    private void saveTransactionLog(Long wasteId, Long sellerId, Long buyerId) {
        transactionLogRepository.save(TransactionLog.builder()
                .wasteId(wasteId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build());
    }
}
